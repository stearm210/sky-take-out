package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
	* 这个sql在xml文件中进行编写
	* */
	//动态sql
	Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

	/*
	* 菜品的批量删除操作
	* 根据主键查询菜品操作
	* */
	@Select("select * from dish where id = #{id}")
	Dish getById(Long id);

	/*
	* 根据主键来删除id
	* */
	@Delete("delete from dish where id = #{id}")
	void deleteById(Long id);

	/*
	* 根据id来批量的删除菜品数据
	* */
	void deleteByIds(List<Long> ids);

	/*
	* 更新菜品信息，建议写成动态sql形式
	* */
	/*
	* 根据id来动态的修改菜品
	* */
	//同时这里写一个自动填充方便开发操作
	@AutoFill(value = OperationType.UPDATE)
	void update(Dish dish);


	/**
	 * 动态条件查询菜品
	 * @param dish
	 * @return
	 */
	List<Dish> list(Dish dish);

	/*
	* 根据套餐id查询菜品
	* */
	@Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
	List<Dish> getBySetmealId(Long setmealId);
}
