package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
	/*
	* 批量插入口味数据
	* */
	void insertBatch(List<DishFlavor> flavors);

	/*
	* 删除关联的套餐表的数据
	* 根据菜品id来删除对应的口味数据
	* */
	@Delete("delete from sky_take_out.dish_flavor where dish_id = #{dishId}")
	void deleteByDishId(Long dishId);

	/*
	* 根据菜品id集合批量删除关联的口味数据
	* */
	void deleteByDishIds(List<Long> dishIds);
}
