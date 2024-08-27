package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
* 查询关系表的mapper接口
* */
@Mapper
public interface SetmealDishMapper {

	/*
	* 根据菜品id来查询套餐id，以方便进行删除操作
	* 如果查到套餐中有这个id，那么list中将会对此进行记录操作
	* */
	//同样，这里的操作也要使用新的xml文件进行编写sql
	List<Long> getSetmealDishIds(List<Long> dishIds);
}
