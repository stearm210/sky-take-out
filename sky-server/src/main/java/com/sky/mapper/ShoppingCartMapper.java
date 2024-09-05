package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface ShoppingCartMapper {
	/*
	 * 动态条件查询sql
	 * */
	List<ShoppingCart> list(ShoppingCart shoppingCart);

	/*
	* 根据id来修改商品的数量
	* */
	@Update("update sky_take_out.shopping_cart set number = #{number} where id = #{id}" )
	void updateNumberById(ShoppingCart shoppingCart);
}
