package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

	/*
	* 批量删除操作
	* */
	@DeleteMapping
	@ApiOperation("菜品的批量删除")
	//@RequestParam 注解，这里主要是用于获取URL前端传入的值
	public Result delete(@RequestParam List<Long> ids){
		log.info("菜品的批量删除:{}",ids);
		dishService.deleteBatch(ids);
		return Result.success();
	}

	/*
	* 根据ip来查询菜品id信息
	* */
	//这是一个detmapping方法，用于查询菜品id对应的信息
	@GetMapping("/{id}")
	@ApiOperation("根据id来查询菜品")
	public Result<DishVO> getById(@PathVariable Long id){
		log.info("根据id来查询菜品:{}",id);
		DishVO dishVO = dishService.getByIdWithFlavor(id);
		return Result.success(dishVO);
	}

	/*
	* 修改菜品和关联的口味信息
	* */
	@PutMapping
	@ApiOperation("修改菜品和关联的口味信息")
	public Result update(@RequestBody DishDTO dishDTO){
		log.info("修改菜品:{}",dishDTO);
		dishService.updateWithFlavor(dishDTO);
		return Result.success();
	}
}