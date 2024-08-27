package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
	@Autowired
	private DishMapper dishMapper;
	@Autowired
	private DishFlavorMapper dishFlavorMapper;


	/*
	* 新增菜品的操作
	* 这里的函数涉及到菜品表和口味表的操作
	* 因此需要对这两个表进行操作
	* 那这样就涉及到了多个表的操作问题，因此需要使用事务进行操作以保证事务的一致性
	* 这种要不全部成功要不全部失败
	* */
	@Transactional
	public void saveWithFlavor(DishDTO dishDTO) {
		//传入菜品数据
		//这里定义了一个菜品类，类中只包含菜品的数据，而不包含味道数据
		Dish dish = new Dish();
		//数据拷贝，从一个类到另一个类
		//这里将菜品的数据拷贝到对应的类中
		BeanUtils.copyProperties(dishDTO,dish);

		//菜品表插入1条数据
		dishMapper.insert(dish);

		//获得DishMapper insert语句返回的id
		Long dishId = dish.getId();


		//口味表插入多条数据
		//从dishDTO类中获取对应的flavors数据
		List<DishFlavor> flavors = dishDTO.getFlavors();
		//当这个flavors集合中拥有数据时
		if (flavors != null && flavors.size() > 0) {
			//批量插入数据的时候，需要进行遍历，对插入的数据进行赋值
			flavors.forEach(dishFlavor -> {
				dishFlavor.setDishId(dishId);
			});
			//向口味表中插入n条数据
			dishFlavorMapper.insertBatch(flavors);
		}
	}

	/*
	* 菜品分页查询
	* */
	@Override
	public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
		//分页查询专用pagehelper
		//startpage中的参数分别为需要查询的页码和查询的个数
		PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
		//最终返回一个dishvo格式的page
		Page<DishVO> page=dishMapper.pageQuery(dishPageQueryDTO);

		//返回总记录数和数据集合
		return new PageResult(page.getTotal(),page.getResult());
	}


	/*
	* 菜品的批量删除操作
	* */
	@Override
	public void deleteBatch(List<Long> ids) {

	}
}