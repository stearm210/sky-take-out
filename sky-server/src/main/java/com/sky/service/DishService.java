package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

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
}
