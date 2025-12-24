package com.sky.service.impl;

import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;


@Service
public class ShopServiceImpl implements ShopService {
    public static final String SHOP_STATUS = "SHOP_STATUS";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 设置状态
     *
     * @param status 状态
     */
    @Override
    public void setStatus(Integer status) {
        stringRedisTemplate.opsForValue().set(SHOP_STATUS, String.valueOf(status));
    }

    /**
     * 获取状态
     *
     * @return {@link Integer }
     */
    @Override
    public Integer getStatus() {
        String status = stringRedisTemplate.opsForValue().get(SHOP_STATUS);
        if(status != null){
            return NumberUtils.parseNumber(status,Integer.class);
        }else{
            return null;
        }
         // Return the shop status from Redis
    }
}
