package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

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

    /**
     * 通过带有风味id获取
     *
     * @param id id
     * @return {@link DishVO }
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 用flavor更新
     *
     * @param dishDTO 碟形dto
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 启动或停止
     *
     * @param status 状态
     * @param id     id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 列表
     *
     * @param categoryId 类别id
     * @return {@link List }<{@link DishVO }>
     */
    List<DishVO> list(Long categoryId);
}
