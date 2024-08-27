package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
* 菜品管理
* */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

	@Autowired
	private DishService dishService;

	/*
	* 新增菜品函数
	* */
	//因为返回json数据格式，因此需要使用@requestbody注解
	@PostMapping
	@ApiOperation("新增菜品")
	public Result save(@RequestBody DishDTO dishDTO){
		log.info("新增菜品:{}",dishDTO);
		dishService.saveWithFlavor(dishDTO);
		return Result.success();
	}

	/*
	* 菜品的分页查询
	* */
	@GetMapping("/page")
	@ApiOperation("菜品分页查询")
	//返回的数据是菜品分页查询的固定格式
	//这里的接口请求参数是Query格式而不是json格式，因此不需要使用@requestbody注解
	public Result<PageResult>page(DishPageQueryDTO dishPageQueryDTO){
		log.info("菜品分页查询:{}",dishPageQueryDTO);
		PageResult pageResult=dishService.pageQuery(dishPageQueryDTO);
		return Result.success(pageResult);
	}
}