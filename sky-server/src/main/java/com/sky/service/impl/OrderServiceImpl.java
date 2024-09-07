package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class OrderServiceImpl implements OrderService {
	//订单表
	@Autowired
	private OrderMapper orderMapper;
	//订单明细表
	@Autowired
	private OrderDetailMapper orderDetailMapper;
	//地址簿表
	@Autowired
	private AddressBookMapper addressBookMapper;
	//购物车表
	@Autowired
	private ShoppingCartMapper shoppingCartMapper;

	/*
	* 用户下单
	* */
	@Override
	public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
		//1.处理各种业务异常(地址簿为空，购物车数据为空)
		AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
		//无法正常下单(地址为空时)
		if (addressBook == null){
			throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
		}
		//用户购物车是否为空
		Long userId = BaseContext.getCurrentId();//获得id
		ShoppingCart shoppingCart = new ShoppingCart();
		shoppingCart.setUserId(userId);
		List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
		//购物车数据为空
		if (shoppingCartList == null || shoppingCartList.size() == 0 ){

			//业务异常
			throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
		}

		//2.向订单表插入一条数据
		//创建一个订单对象
		Orders orders = new Orders();
		//对象的属性拷贝进行填充
		BeanUtils.copyProperties(ordersSubmitDTO,orders);
		//设置当前时间
		orders.setOrderTime(LocalDateTime.now());
		//设置支付状态
		orders.setPayStatus(Orders.UN_PAID);
		//设置状态
		orders.setStatus(Orders.PENDING_PAYMENT);
		//订单号
		orders.setNumber(String.valueOf(System.currentTimeMillis()));
		//手机号
		orders.setPhone(addressBook.getPhone());
		//收货人
		orders.setConsignee(addressBook.getConsignee());
		//当前订单属于哪个用户
		orders.setUserId(userId);
		orderMapper.insert(orders);

		//3.向订单明细表插入n条数据


		//4.清空当前用户的购物车数据


		//5.封装VO返回的结果


		return null;
	}
}