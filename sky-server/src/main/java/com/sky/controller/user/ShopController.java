package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

//用户端的接口编写
//指定名字，防止同名类冲突
@RestController("userShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController{
	//常量设置，规范代码
	public static final String KEY = "SHOP_STATUS";

	@Autowired
	private RedisTemplate redisTemplate;
	//获取店铺的状态
	@GetMapping("/status")
	@ApiOperation("获取店铺的营业状态")
	public Result<Integer> getStatus(){
		//类型转换
		Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
		log.info("获取到店铺的营业状态为:{}",status == 1 ? "营业中" : "打样中");
		return Result.success(status);
	}
}