package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
	@Autowired
	private DishMapper dishMapper;
	@Autowired
	private SetmealMapper setmealMapper;

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
		}else {
			//如果商品在购物车中不存在，则需要在购物车中插入一条数据
			//需要判断传入的是菜品还是套餐，菜品查菜品表，套餐查套餐表
			//判断的方法是：判断是否存在对应的id即可

			//进行判断
			Long dishId = shoppingCartDTO.getDishId();
			if (dishId != null){
				//本次添加到购物车的是菜品
				Dish dish = dishMapper.getById(dishId);
				shoppingCart.setName(dish.getName());
				shoppingCart.setImage(dish.getImage());
				shoppingCart.setAmount(dish.getPrice());
			}else {
				//本次添加到购物车的是套餐
				Long setmealId = shoppingCartDTO.getSetmealId();
				Setmeal setmeal = setmealMapper.getById(setmealId);
				shoppingCart.setName(setmeal.getName());
				shoppingCart.setImage(setmeal.getImage());
				shoppingCart.setAmount(setmeal.getPrice());
			}
			//插入的数量设置为一
			shoppingCart.setNumber(1);
			shoppingCart.setCreateTime(LocalDateTime.now());
			shoppingCartMapper.insert(shoppingCart);
		}

	}

	/*
	* 查看购物车
	* */
	@Override
	public List<ShoppingCart> showShoppingCart() {
		//获取到当前微信用户的id
		Long userId = BaseContext.getCurrentId();
		//构造对应的shoppingCart对象(由于返回值与ShoppingCart有关)
		//传用户id进行查询即可
		ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();

		List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
		return list;
	}
}