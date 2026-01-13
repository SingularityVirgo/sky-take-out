package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO 菜品页面查询dto
     * @return {@link Page }<{@link DishVO }>
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 按id获取
     *
     * @param id id
     * @return {@link Dish }
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);
    /**
     * 根据主键删除
     *
     * @param id id
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 删除批次
     *
     * @param ids 身份证
     */
    void deleteBatch(List<Long> ids);

    /**
     * 更新
     *
     * @param dish 碟子
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 列表
     *
     * @param categoryId 类别id
     * @return {@link List }<{@link DishVO }>
     */
    List<DishVO> list(Long categoryId);

    /**
     * 列表
     *
     * @param dish 碟子
     * @return {@link List }<{@link Dish }>
     */
    List<DishVO> list(Dish dish);
    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
