package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

	/*
	* 插入员工数据，属于单表的新增操作
	* */
	@Insert("insert into employee (name, username,password, phone, sex, id_number, create_time, update_time, create_user, update_user,status)" +
			"values "+ "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
	//自定义注解的insert拦截
	@AutoFill(value = OperationType.INSERT)
	void insert(Employee employee);

	/*
	* 分页查询的方法
	* */
	//这里使用到了动态SQL，因此建议在xml映射文件中编写对应的SQL代码
	Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

	/*
	*根据主键来动态的调整和修改语句
	* */
	//自定义注解的insert拦截
	@AutoFill(value = OperationType.UPDATE)
	void update(Employee employee);

	/*
	* 根据id来查询对应的员工信息
	* */
	@Select("select * from employee where id=#{id}")
	Employee getById(Long id);
}
