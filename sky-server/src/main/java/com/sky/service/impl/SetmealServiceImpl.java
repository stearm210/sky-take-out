package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
* 套餐业务的实现
* */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
	@Autowired
	private SetmealMapper setmealMapper;
	@Autowired
	private SetmealDishMapper setmealDishMapper;
	@Autowired
	private DishMapper dishMapper;
	/*
	* 新增套餐，同时需要保存套餐和菜品之间的关系
	* */
	//使用事务进行操作
	//事务要不一起成功，要不一起失败
	@Transactional
	public void saveWithDish(SetmealDTO setmealDTO) {
		Setmeal setmeal = new Setmeal();
		BeanUtils.copyProperties(setmealDTO, setmeal);

		//向套餐表插入数据
		setmealMapper.insert(setmeal);

		//获取生成的套餐id
		Long setmealId = setmeal.getId();

		List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
		setmealDishes.forEach(setmealDish -> {
			setmealDish.setSetmealId(setmealId);
		});

		//保存套餐和菜品的关联关系
		setmealDishMapper.insertBatch(setmealDishes);
	}

	/*
	* 套餐的分页查询
	* */
	@Override
	public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
		int pageNum = setmealPageQueryDTO.getPage();
		int pageSize = setmealPageQueryDTO.getPageSize();

		PageHelper.startPage(pageNum, pageSize);
		Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
		return new PageResult(page.getTotal(), page.getResult());
	}
}