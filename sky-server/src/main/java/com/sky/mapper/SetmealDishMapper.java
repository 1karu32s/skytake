package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface SetmealDishMapper {

    @Select("select count(id) from sky_take_out.setmeal_dish where dish_id = #{id}")
    Integer countByDishId(Long id);

    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除套餐和菜品的关联关系
     * @param setmealId
     */
    @Delete("delete from sky_take_out.setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    @Select("select * from sky_take_out.setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);

    @Select("select setmeal_id from sky_take_out.setmeal_dish where dish_id = #{id}")
    List<Long> getSetmealIdByDishId(Long id);


    /**
     * 根据套餐id查询套餐和菜品的关联关系
     * @param setmealId
     * @return
     */

}
