package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
* 套餐管理
* */
@RestController
//请求参数
//主要用于调用这个请求的mapping进行操作，基本上是每个新class必备的操作
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {
	@Autowired
	private SetmealService setmealService;
	/*
	* 新增套餐的操作
	* */
	@PostMapping
	@ApiOperation("新增套餐")
	//新增套餐，返回json格式
	//使用SetmealDTO规定格式问题
	public Result save(@RequestBody SetmealDTO setmealDTO){
		setmealService.saveWithDish(setmealDTO);
		return Result.success();
	}

	/*
	* 套餐的分页查询
	* */
	/**
	 * 分页查询
	 * @param setmealPageQueryDTO
	 * @return
	 */
	@GetMapping("/page")
	@ApiOperation("分页查询")
	public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
		PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
		return Result.success(pageResult);
	}

}