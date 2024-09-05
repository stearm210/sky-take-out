package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
	@Autowired
	private ShoppingCartMapper shoppingCartMapper;
	/*
	* 添加购物车的实现方法
	*
	* */
	@Override
	public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
		//判断当前加入购物车的商品在购物车中是否存在
		ShoppingCart shoppingCart = new ShoppingCart();
		//对象的属性拷贝进行赋值
		BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
		//通过拦截器获得当前登录的微信用户的id
		Long userId = BaseContext.getCurrentId();
		//通过setUserId方法传入到shoppingCart对象中
		shoppingCart.setUserId(userId);

		List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

		//如果商品在购物车中存在，数量加一
		if (list != null && list.size() > 0){
			//这个时候只需要获得第一条数据就行，直接对着第一条数据加一
			ShoppingCart cart = list.get(0);
			cart.setNumber(cart.getNumber() + 1);//数量加一之后，只需要执行update更新一下数据库就可以了
			shoppingCartMapper.updateNumberById(cart);
		}

		//如果不存在，需要插入一条购物车数据
	}
}