package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "C端购物车相关接口")
public class ShoppingCartController {
	@Autowired
	private ShoppingCartService shoppingCartService;

	//返回一个json格式
	//这里的Result不使用泛型，主要是没有对应的要求
	//如果使用的查询操作，一般来说返回的数据是有一定要求的，因此一般会使用泛型
	@PostMapping("/add")
	@ApiOperation("添加购物车")
	public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
		log.info("添加购物车，商品信息为:{}", shoppingCartDTO);
		shoppingCartService.addShoppingCart(shoppingCartDTO);
		return Result.success();
	}

	/*
	* 查看购物车
	* */
	@GetMapping("/list")
	@ApiOperation("查看购物车")
	public Result<List<ShoppingCart>> list(){
		List<ShoppingCart> list = shoppingCartService.showShoppingCart();
		return Result.success(list);
	}

	/*
	* 清空购物车
	* */
	@DeleteMapping("/clean")
	@ApiOperation("清空购物车")
	public Result clean(){
		shoppingCartService.cleanShoppingCart();
		return Result.success();
	}

	/*
	* 删除购物车中的一个商品
	* */
	@PostMapping("/sub")
	@ApiOperation("删除购物车中的一个商品")
	public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
		log.info("删除购物车中的一个商品，商品：:{}", shoppingCartDTO);
		shoppingCartService.subShoppingCart(shoppingCartDTO);
		return Result.success();
	}
}