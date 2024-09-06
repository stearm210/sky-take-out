package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
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

	/*
	* 插入购物车数据
	* */
	@Insert("insert into sky_take_out.shopping_cart(name, user_id, dish_id, setmeal_id, dish_flavor, number, amount, image, create_time)"+ "VALUES (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})")
	void insert(ShoppingCart shoppingCart);

	/*
	* 清空购物车操作
	* */
	@Delete("delete from sky_take_out.shopping_cart where user_id = #{userId}")
	void deleteByUserId(Long userId);

	/*
	* 删除购物车中的一个商品
	* */
	@Delete("delete from shopping_cart where id = #{id}")
	void deleteById(Long id);
}
