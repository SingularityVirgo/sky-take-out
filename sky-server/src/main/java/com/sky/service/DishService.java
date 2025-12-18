package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

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

    /**
     * 页面查询
     *
     * @param dishPageQueryDTO 菜品页面查询dto
     * @return {@link PageResult }
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除
     *
     * @param ids 身份证
     */
    void deleteBatch(List<Long> ids);
}
