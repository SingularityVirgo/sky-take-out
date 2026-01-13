package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 插入批处理
     *
     * @param flavors 香精
     */

    void insertBatch(List<DishFlavor> flavors);

    /**
     * 按菜式id删除
     *
     * @param dishId 身份证
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 按碟子id删除
     *
     * @param dishIds 身份证
     */
    void deleteByDishIds(List<Long> dishIds);

    /**
     * 通过dish id获取
     *
     * @param dishId dishId
     * @return {@link List }<{@link DishFlavor }>
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}
