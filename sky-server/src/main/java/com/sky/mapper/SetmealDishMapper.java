package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealDishMapper {

    @Select("select count(id) from sky_take_out.setmeal_dish where dish_id = #{id}")
    Integer countByDishId(Long id);
}
