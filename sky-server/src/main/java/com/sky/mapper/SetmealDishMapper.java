package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 通过菜肴id获取套餐 id
     *
     * @param dishIds 碟子id
     * @return {@link List }<{@link Long }>
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}
