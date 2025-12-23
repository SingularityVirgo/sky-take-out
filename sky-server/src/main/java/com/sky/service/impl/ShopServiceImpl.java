package com.sky.service.impl;

import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {
    public static final String SHOP_STATUS = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 设置状态
     *
     * @param status 状态
     */
    @Override
    public void setStatus(Integer status) {
        redisTemplate.opsForValue().set(SHOP_STATUS,status);
    }

    @Override
    public Integer getStatus() {
        return (Integer) redisTemplate.opsForValue().get(SHOP_STATUS); // Return the shop status from Redis
    }
}
