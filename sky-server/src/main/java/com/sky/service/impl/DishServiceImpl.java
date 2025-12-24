package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;

import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sky.constant.RedisConstant.CACHE_DISH_LIST_KEY;

/**
 * 碟形服务实施
 *
 * @author maziy
 * @date 2025/12/18
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 保存与风味
     *
     * @param dishDTO 碟形dto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //向菜品表插入1条数据
        dishMapper.insert(dish);
        //获取生成的菜品id
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
            //向菜品口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
        stringRedisTemplate.delete(CACHE_DISH_LIST_KEY + dishDTO.getCategoryId());

    }

    /**
     * 分页查询
     *
     * @param dishPageQueryDTO 菜品页面查询dto
     * @return {@link PageResult }
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 菜品批量删除
     *
     * @param ids 身份证
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能删除--删除的菜品状态不能是启售状态
        for(Long id:ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            //判断当前菜品是否关联了套餐
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
            if(setmealIds != null && setmealIds.size() > 0){
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }
//        for(Long id:ids){
//            //删除菜品表中的菜品数据
//            dishMapper.deleteById(id);
//            //删除菜品口味表数据
//            dishFlavorMapper.deleteByDishId(id);
//        }
        //批量删除菜品表中的菜品数据
        dishMapper.deleteBatch(ids);
        //批量删除菜品口味表数据
        dishFlavorMapper.deleteByDishIds(ids);
        //删除缓存数据
        deleteAllDishCategoryWithLua();

    }

    /**
     * 通过带有风味id获取
     *
     * @param id id
     * @return {@link DishVO }
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);
        //根据id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        //封装
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 用flavor更新
     *
     * @param dishDTO 碟形dto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //修改菜品表数据
        dishMapper.update(dish);
        //删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //重新插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
       deleteAllDishCategoryWithLua();


    }

    /**
     * 启动或停止
     *
     * @param status 状态
     * @param id     id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.update(dish);
        deleteAllDishCategoryWithLua();
    }

    /**
     * 列表
     *
     * @param categoryId 类别id
     * @return {@link List }<{@link DishVO }>
     */
    @Override

    public List<DishVO> list(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        List<DishVO> list = dishMapper.list(categoryId);
        return list;

    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {

        String key = CACHE_DISH_LIST_KEY + dish.getCategoryId();
        // 查询redis中是否缓存菜品数据
        String json = stringRedisTemplate.opsForValue().get(key);
        // 缓存存在，直接返回，无需查询数据库
        if (json != null && json.length() > 0) {
            return JSON.parseArray(json, DishVO.class);
        }
        // 缓存不存在，将数据查询数据库，然后缓存
        List<DishVO> dishVOList = dishMapper.list(dish);
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(dishVOList));
        List<DishVO> newDishVOList = new ArrayList<>();

        for (DishVO dishVO : dishVOList) {
            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(dishVO.getId());

            dishVO.setFlavors(flavors);
            newDishVOList.add(dishVO);
        }

        return newDishVOList;
    }

    private void deleteAllDishCategoryWithLua() {
        String luaScript =
                "local keys = redis.call('KEYS', ARGV[1]) " +
                        "for i=1,#keys,5000 do " +
                        "   redis.call('DEL', unpack(keys, i, math.min(i+4999, #keys))) " +
                        "end " +
                        "return #keys";

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(luaScript);
        script.setResultType(Long.class);

        // 注意：KEYS 命令的第一个参数必须是 key 数量，这里传 0
        Long deletedCount = stringRedisTemplate.execute(script,
                Collections.emptyList(),
                "dish:list:category:*");

        log.info("Lua脚本批量删除缓存数量: {}", deletedCount);
    }
    /*public void deleteAllDishCategoryCache() {
        String pattern = "dish:list:category:*";

        try (Cursor<byte[]> cursor = stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .scan(ScanOptions.scanOptions().match(pattern).count(1000).build())) {

            while (cursor.hasNext()) {
                byte[] keyBytes = cursor.next();
                String key = new String(keyBytes, StandardCharsets.UTF_8);
                stringRedisTemplate.delete(key);
                log.info("删除缓存: {}", key);
            }
        } catch (Exception e) {
            log.error("批量删除缓存失败", e);
        }
    }*/
}
