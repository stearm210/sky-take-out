package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


	/*
	* 菜品数据插入
	* */
	//
	@AutoFill(value = OperationType.INSERT)
	void insert(Dish dish);

	/*
	* 菜品的分页查询
	* */
	//动态sql
	Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);
}
