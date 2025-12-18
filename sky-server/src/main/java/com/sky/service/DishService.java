package com.sky.service;

import com.sky.dto.DishDTO;

/**
 * 菜式服务
 *
 * @author maziy
 * @date 2025/12/18
 */
public interface DishService {
    /**
     * 新增菜品及其口味
     *
     * @param dishDTO 碟形dto
     */
    void saveWithFlavor(DishDTO dishDTO);
}
