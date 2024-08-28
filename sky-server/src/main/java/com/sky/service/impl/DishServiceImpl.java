package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.SetmealDishMapper;
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

	//注入菜品表关联查询的套餐表查询对象
	@Autowired
	private SetmealDishMapper setmealDishMapper;


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
		BeanUtils.copyProperties(dishDTO, dish);

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
		PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
		//最终返回一个dishvo格式的page
		Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

		//返回总记录数和数据集合
		return new PageResult(page.getTotal(), page.getResult());
	}


	/*
	 * 菜品的批量删除操作
	 * */
	@Override
	public void deleteBatch(List<Long> ids) {
		// 判断当前的菜品是否能够删除--是否存在起售中的菜品
		for (Long id : ids) {
			//查询将要删除的菜品的id，确保删除
			Dish dish = dishMapper.getById(id);
			if (dish.getStatus() == StatusConstant.ENABLE) {
				//当前的菜品处于起售状态，不能删除
				//因此这里抛出异常
				throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);

			}
		}

		//判断当前的菜品是否能够删除--是否被套餐关联
		//由于查询的套餐关系表是一个新的表，因此需要新设置一个mapper进行查询,也就是说需要查询是否有套餐表进行关联
		List<Long> setmealIds = setmealDishMapper.getSetmealDishIds(ids);
		//如果在套餐中查到对应的id，那就是表明确实有关联
		if (setmealIds != null && setmealIds.size() > 0) {
			//当前菜品被套餐关联了，不能删除
			throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
		}


		//删除菜品表中的菜品数据
		//遍历集合，主要用于删除
//		for (Long id : ids) {
//			dishMapper.deleteById(id);
//			//删除菜品关联的口味数据
//			dishFlavorMapper.deleteByDishId(id);
//		}

		//根据菜品id集合批量删除菜品数据
		//sql:delete from dish where id in(?,?,?)
		dishMapper.deleteByIds(ids);

		//根据菜品id集合批量删除关联的口味数据
		//sql:delete from dish_flavor where dish_id in(?,?,?)
		dishFlavorMapper.deleteByDishIds(ids);
	}

	/*
	 * 根据id来查询菜品和对应的口味数据
	 * */

	public DishVO getByIdWithFlavor(Long id) {
		//根据id查询菜品数据
		Dish dish = dishMapper.getById(id);

		//根据菜品id查询口味数据
		List<DishFlavor> dishFlavors = dishFlavorMapper.deleteByDishId(id);

		//将查到的数据进行对应的封装
		//将查询到的数据封装到VO
		//基础的属性进行拷贝
		DishVO dishVO = new DishVO();
		BeanUtils.copyProperties(dish, dishVO);//将菜品的数据赋值进去
		//之后将菜品id查到的口味数据一起赋值进dishVO重
		dishVO.setFlavors(dishFlavors);
		return dishVO;
	}
}