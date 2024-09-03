package com.sky.controller.user;

/*
* 用户端登录
* */

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
//使用request接口
//使用request进行请求服务
@RequestMapping("/user/user")
@Api(tags = "C端用户常用接口")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	//jwt令牌的生成全部细节会封装到这个类中
	@Autowired
	private JwtProperties jwtProperties;
	/*
	* 微信登录操作
	* */
	@PostMapping("/login")
	@ApiOperation("微信登录操作")
	//返回一个json文件
	public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
		log.info("微信用户登录:{}",userLoginDTO.getCode());

		//调用微信登录
		User user = userService.wxLogin(userLoginDTO);

		//jwt令牌生成代码
		//为微信用户生成jwt令牌
		//用户之唯一标识
		Map<String, Object> claims = new HashMap<>();
		//使用map集合放入得到的userid
		claims.put(JwtClaimsConstant.USER_ID, user.getId());
		//生成对应的令牌,这里将claims进行注入拼接
		String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);

		//创建一个userloginvo已经定义好模版
		UserLoginVO userLoginVO = UserLoginVO.builder()
				.id(user.getId())
				.openid(user.getOpenid())
				.token(token)
				.build();
		return Result.success(userLoginVO);
	}
}