package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/*
* 定时处理订单状态
* */
@Component
@Slf4j
public class OrderTask {
	@Autowired
	private OrderMapper orderMapper;
	/*
	* 处理超时订单
	* */
	//每分钟触发一次
	@Scheduled(cron = "0 * * * * ? ")
	public void processTimeoutOrder(){
		log.info("定时处理超时订单:{}", LocalDateTime.now());
		//超时订单问题
		//当前时间减15分钟
		LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
		List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);

		//如果有超时订单，则执行以下操作
		if (ordersList != null && ordersList.size() > 0) {
			for (Orders orders : ordersList) {
				//更变订单的状态
				orders.setStatus(Orders.CANCELLED);
				//订单取消的原因
				orders.setCancelReason("订单超时，自动取消");
				//取消时间
				orders.setCancelTime(LocalDateTime.now());
				orderMapper.update(orders);
			}
		}
	}

	/*
	* 处理一直处于派送中的订单
	* */
	//每天凌晨一点触发一次
	@Scheduled(cron = "0 0 1 * * ?")
	public void processDeliveryOrder(){
		log.info("定时处理一直处于派送中的订单:{}", LocalDateTime.now());
		//计算时间,上一个工作日一直处于派送中的订单
		LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
		List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);
		if (ordersList != null && ordersList.size() > 0) {
			for (Orders orders : ordersList) {
				//更变订单的状态
				orders.setStatus(Orders.COMPLETED);
				orderMapper.update(orders);
			}
		}
	}
}