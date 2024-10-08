package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/*
* 新增菜品之操作
* */
public interface DishService {
	/*
	* 新增菜品和对应的口味接口
	* */
	public void saveWithFlavor(DishDTO dishDTO);

	/*
	*菜品分页查询
	* */
	PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

	/*
	* 菜品的批量删除操作
	* */
	void deleteBatch(List<Long> ids);

	/*
	* 根据id来查询菜品和对应的口味数据
	* */
	DishVO getByIdWithFlavor(Long id);

	/*
	* 根据id来修改菜品基本信息和对应的口味信息
	* */
	void updateWithFlavor(DishDTO dishDTO);


	/*
	* 条件查询菜品和口味数据
	* */
	List<DishVO> listWithFlavor(Dish dish);

	/*
	* 菜品的起售与停售
	* */
	void startOrStop(Integer status, Long id);

	/*
	* 根据分类id来查询菜品信息
	* */
	List<Dish> list(Long categoryId);
}
