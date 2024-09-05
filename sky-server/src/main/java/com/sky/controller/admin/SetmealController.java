package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

	//缓存操作，这里是清理缓存
	//精确的清理对应的缓存数据
	@CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")

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

	/*
	* 套餐之删除操作
	* */
	@DeleteMapping
	@ApiOperation("批量删除套餐")

	//注解清理缓存数据，这里选择全部清理
	@CacheEvict(cacheNames = "setmealCache",allEntries = true)

	public Result delete(@RequestParam List<Long> ids){
		setmealService.deleteBatch(ids);
		return Result.success();
	}

	/*
	* 根据id查询套餐，用于修改页面回显数据
	* */
	@GetMapping("/{id}")
	@ApiOperation("根据id查询套餐")
	public Result<SetmealVO> getById(@PathVariable Long id) {
		SetmealVO setmealVO = setmealService.getByIdWithDish(id);
		return Result.success(setmealVO);
	}

	/**
	 * 修改套餐
	 *
	 * @param setmealDTO
	 * @return
	 */
	@PutMapping
	@ApiOperation("修改套餐")
	//注解清理缓存数据，这里选择全部清理
	@CacheEvict(cacheNames = "setmealCache",allEntries = true)
	public Result update(@RequestBody SetmealDTO setmealDTO) {
		setmealService.update(setmealDTO);
		return Result.success();
	}

	/**
	 * 套餐起售停售
	 * @param status
	 * @param id
	 * @return
	 */
	@PostMapping("/status/{status}")
	@ApiOperation("套餐起售停售")
	//注解清理缓存数据，这里选择全部清理
	@CacheEvict(cacheNames = "setmealCache",allEntries = true)
	public Result startOrStop(@PathVariable Integer status, Long id) {
		setmealService.startOrStop(status, id);
		return Result.success();
	}

}