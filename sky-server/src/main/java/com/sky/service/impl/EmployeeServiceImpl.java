package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    //注入操作
    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端传过来的明文密码进行md5加密处理
        //使用工具类对密码进行比对
        //改造时同样需要在数据库中进行改造
        password = DigestUtils.md5DigestAsHex(password.getBytes());


        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /*
    * 新增员工实现类方法
    * */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        /*
        * 这里将新增员工的所有信息进行定义，分别进行赋值等操作
        * */

        //输出当前线程的id
        System.out.println("当前线程的ID输出:"+Thread.currentThread().getId());

        //由于前端传入的类型与实际后端需要进行操作的类型是不一致的，因此需要进行强制类型转换
        Employee employee=new Employee();

        //后面代码中展示的都是之后需要自己添加的，而不是通过前端传入的，前端传入的默认已经添加完成
        //使用对象的属性拷贝将数据批量的进行赋值
        //这样的前提是属性名是必须一致的前提
        BeanUtils.copyProperties(employeeDTO,employee);

        //设置账号的状态，默认是正常的,这里调用了常量类
        employee.setStatus(StatusConstant.ENABLE);

        //设置密码，默认的密码是123456
        //这个密码要放到数据库中，因此这个密码需要进行加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //设置当前记录的创建时间和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //设置当前创建人id和修改人的id
        //前面拦截器中已经将用户的ID存入储存中，这里从线程的存储中获得对应的用户id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        //调用dao层进行注入调用
        employeeMapper.insert(employee);
    }

    /*
    * 分页查询的具体实现
    * */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //开始分页查询
        //使用PageHelper开始查询分页
        //这里分别传送了分页以及页码的大小问题
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        return null;
    }

}
