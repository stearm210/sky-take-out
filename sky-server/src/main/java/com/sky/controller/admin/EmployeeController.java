package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
//通过注解控制生成接口文档
@Api(tags="员工相关接口")

public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;
	@Qualifier("reactiveRedisTemplate")
	@Autowired
	private ReactiveRedisTemplate reactiveRedisTemplate;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    //提交过来的是json格式的数据，因此需要加上@requestbody的注解
    @PostMapping("/login")
    @ApiOperation(value = "员工登录功能")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);//token源于jwtproperties文件中的函数调用

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出功能")
    public Result<String> logout() {
        return Result.success();
    }

    /*
    * 新增员工功能开发
    * */
    //提交过来的是json格式的数据，因此需要加上@requestbody的注解
    @PostMapping
    //加上接口文档
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工:{}", employeeDTO);//新增员工之操作

        //输出当前线程的id
        System.out.println("当前线程的ID输出:"+Thread.currentThread().getId());

        employeeService.save(employeeDTO);
        return Result.success();
    }

    /*
    * 分页查询
    * */
    @GetMapping("/page")
    //接口文档
    @ApiOperation("员工分页查询")
    //DTO进行了对应的分装
    //由于数据格式不是json格式，因此不需要进行#requestbody封装
    //EmployeePageQueryDTO为分页查询最终返回数(Query)需要返回的格式内容
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询，参数为:{}", employeePageQueryDTO);
        //最终返回PageResult对象
        PageResult pageResult=employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }


    /*
    * 启用禁用员工账号
    * */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用员工账号")
    //这里使用的是路径参数(status放在了占位符中，因此需要使用@pathvariable进行调用)，因此需要使用PathVariable注解
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("启用禁用员工账号:{},{}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }

    /*
    * 根据id来查询员工数据
    * */
    @GetMapping("/{id}")
    @ApiOperation("根据id来查询员工信息")
    public Result<Employee> getById(@PathVariable Long id){
        Employee employee=employeeService.getById(id);
        return Result.success(employee);
    }

    /*
    * 编辑员工信息
    * */
    @PutMapping
    @ApiOperation("编辑员工信息")
    //这里使用之前使用过的EmployeeDTO格式来接收对应的数据
    //一般来说，requestbody用于json格式的对象接收
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息:{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

}
