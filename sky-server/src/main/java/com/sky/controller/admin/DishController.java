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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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

	//管理端同样需要对redis数据的缓存进行编写
	//这样可以方便管理端对数据进行管理
	@Autowired
	private RedisTemplate redisTemplate;

	/*
	* 新增菜品函数
	* */
	//因为返回json数据格式，因此需要使用@requestbody注解
	@PostMapping
	@ApiOperation("新增菜品")
	public Result save(@RequestBody DishDTO dishDTO){
		log.info("新增菜品:{}",dishDTO);
		dishService.saveWithFlavor(dishDTO);

		//新增数据需要清理redis缓存数据
		String key = "dish_" + dishDTO.getCategoryId();
		//调用统一的清理缓存方法,这里只是删除了某一个key
		cleanCache(key);

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

		//调用统一的清理缓存方法
		cleanCache("dish_");

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

		//调用统一的清理缓存方法
		cleanCache("dish_");

		return Result.success();
	}

	/*
	* 菜品的起售和停售
	* */
	@PostMapping("/status/{status}")
	@ApiOperation("菜品起售停售")
	//使用路径参数，用于判断{status}
	///这里的id是指菜品的id
	public Result<String> startOrStop(@PathVariable Integer status, Long id){
		dishService.startOrStop(status,id);

		//调用统一的清理缓存方法
		cleanCache("dish_");

		return Result.success();
	}


	/*
	 * 根据分类id查询菜品信息
	 * */
	@GetMapping("/list")
	@ApiOperation("根据分类id查询菜品")
	public Result<List<Dish>> list(Long categoryId){
		List<Dish> list = dishService.list(categoryId);
		return Result.success(list);
	}

	/*
	* 统一清理缓存数据的方法
	* */
	private void cleanCache(String pattern){
		//将所有的菜品缓存数据清理，这里清理所有以dish_开头的key
		Set keys = redisTemplate.keys(pattern);//首先查询到对应所有的key
		//全部查询完成之后，再进删除操作
		redisTemplate.delete(keys);
	}

}