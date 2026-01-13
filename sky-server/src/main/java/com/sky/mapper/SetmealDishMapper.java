package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    /**
     * 保存setmeal菜肴
     *
     * @param setmealDishes setmeal菜肴
     */
    void saveSetmealDishes(List<SetmealDish> setmealDishes);

    /**
     * 通过setmeal id获取setmeal菜肴
     *
     * @param id id
     * @return {@link List }<{@link SetmealDish }>
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getSetmealDishesBySetmealId(Long id);

    /**
     * 按setmeal id删除
     *
     * @param id id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long id);

    /**
     * 按setmeal id删除批处理
     *
     * @param ids 身份证
     */
    void deleteBatchBySetmealIds(List<Long> ids);
}
