package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.sky.constant.RedisConstant.CART_KEY_PREFIX;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;


    /**
     * 1. 添加到购物车（核心方法）
     *
     * @param cartDTO 手推车dto
     */
    @Transactional
    public void add(ShoppingCartDTO cartDTO) {
        // 参数校验
        if (cartDTO.getDishId() == null && cartDTO.getSetmealId() == null) {
            throw new IllegalArgumentException("必须指定菜品或套餐");
        }

        Long userId = BaseContext.getCurrentId(); // 从 ThreadLocal 获取当前用户
        String key = CART_KEY_PREFIX + userId;

        // 生成唯一标识
        String field = generateCartField(cartDTO);

        // 检查是否已存在
        String existingJson = (String) stringRedisTemplate.opsForHash().get(key, field);

        if (existingJson != null) {
            // 已存在：数量 +1
            ShoppingCart existingCart = JSON.parseObject(existingJson, ShoppingCart.class);
            existingCart.setNumber(existingCart.getNumber() + 1);
            stringRedisTemplate.opsForHash().put(key, field, JSON.toJSONString(existingCart));
        } else {
            // 不存在：新建条目
            ShoppingCart newCart = buildShoppingCart(cartDTO, userId);
            stringRedisTemplate.opsForHash().put(key, field, JSON.toJSONString(newCart));
        }

        // 设置过期时间（7天）
        stringRedisTemplate.expire(key, 7, TimeUnit.DAYS);

        log.info("用户 {} 添加购物车：{}", userId, field);
    }

    /**
     * 2. 查询购物车列表
     *
     * @return {@link List }<{@link ShoppingCart }>
     */
    public List<ShoppingCart> listCart() {
        Long userId = BaseContext.getCurrentId();
        String key = CART_KEY_PREFIX + userId;

        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);

        List<ShoppingCart> cartList = new ArrayList<>();
        for (Object value : entries.values()) {
            ShoppingCart cart = JSON.parseObject(value.toString(), ShoppingCart.class);
            cartList.add(cart);
        }

        // 按添加时间排序
        cartList.sort(Comparator.comparing(ShoppingCart::getCreateTime).reversed());

        return cartList;
    }

    /**
     * 3. 修改商品数量
     *
     * @param cartDTO  手推车dto
     * @param quantity 数量
     */
    public void updateQuantity(ShoppingCartDTO cartDTO, Integer quantity) {
        if (quantity <= 0) {
            deleteFromCart(cartDTO);
            return;
        }

        Long userId = BaseContext.getCurrentId();
        String key = CART_KEY_PREFIX + userId;
        String field = generateCartField(cartDTO);

        String json = (String) stringRedisTemplate.opsForHash().get(key, field);
        if (json == null) {
            throw new RuntimeException("购物车中不存在该商品");
        }

        ShoppingCart cart = JSON.parseObject(json, ShoppingCart.class);
        cart.setNumber(quantity);
        stringRedisTemplate.opsForHash().put(key, field, JSON.toJSONString(cart));

        log.info("用户 {} 修改商品 {} 数量为 {}", userId, field, quantity);
    }

    /**
     * 4. 删除单个商品
     *
     * @param cartDTO 手推车dto
     */
    public void deleteFromCart(ShoppingCartDTO cartDTO) {
        Long userId = BaseContext.getCurrentId();
        String key = CART_KEY_PREFIX + userId;
        String field = generateCartField(cartDTO);

        stringRedisTemplate.opsForHash().delete(key, field);

        log.info("用户 {} 删除购物车商品: {}", userId, field);
    }

    /**
     * 5. 清空购物车
     */
    public void clearCart() {
        Long userId = BaseContext.getCurrentId();
        String key = CART_KEY_PREFIX + userId;

        stringRedisTemplate.delete(key);

        log.info("用户 {} 清空购物车", userId);
    }

    /**
     * 6. 获取购物车商品总数
     *
     * @return {@link Integer }
     */
    public Integer getTotalCount() {
        Long userId = BaseContext.getCurrentId();
        String key = CART_KEY_PREFIX + userId;

        return stringRedisTemplate.opsForHash().values(key).stream()
                .map(obj -> JSON.parseObject(obj.toString(), ShoppingCart.class).getNumber())
                .reduce(0, Integer::sum);
    }

    /**
     * 生成购物车 Field（唯一标识）
     *
     * @param cartDTO 手推车dto
     * @return {@link String }
     */
    private String generateCartField(ShoppingCartDTO cartDTO) {
        String flavor = cartDTO.getDishFlavor() != null ? cartDTO.getDishFlavor() : "";

        if (cartDTO.getDishId() != null) {
            // 菜品格式: dish:123:微辣
            return String.format("dish:%d:%s", cartDTO.getDishId(), flavor);
        } else {
            // 套餐格式: setmeal:456:
            return String.format("setmeal:%d:", cartDTO.getSetmealId());
        }
    }

    /**
     * 构建 ShoppingCart 对象（首次添加时）
     *
     * @param cartDTO 手推车dto
     * @param userId  用户id
     * @return {@link ShoppingCart }
     */
    private ShoppingCart buildShoppingCart(ShoppingCartDTO cartDTO, Long userId) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(userId);
        cart.setDishFlavor(cartDTO.getDishFlavor());
        cart.setNumber(1); // 首次添加数量为1
        cart.setCreateTime(LocalDateTime.now());

        if (cartDTO.getDishId() != null) {
            // 查询菜品信息
            Dish dish = dishMapper.getById(cartDTO.getDishId());
            if (dish == null) {
                throw new RuntimeException("菜品不存在");
            }
            cart.setDishId(dish.getId());
            cart.setName(dish.getName());
            cart.setAmount(dish.getPrice());
            cart.setImage(dish.getImage());
        } else {
            // 查询套餐信息
            Setmeal setmeal = setmealMapper.getById(cartDTO.getSetmealId());
            if (setmeal == null) {
                throw new RuntimeException("套餐不存在");
            }
            cart.setSetmealId(setmeal.getId());
            cart.setName(setmeal.getName());
            cart.setAmount(setmeal.getPrice());
            cart.setImage(setmeal.getImage());
        }

        return cart;
    }
}
