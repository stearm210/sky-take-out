package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
* 动态条件查询sql
* */
@Mapper
public interface ShoppingCartMapper {
	List<ShoppingCart> list(ShoppingCart shoppingCart);
}
