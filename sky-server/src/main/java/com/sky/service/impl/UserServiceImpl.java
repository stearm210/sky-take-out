package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
	//微信服务接口地址常量
	public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

	//注入配置属性类
	@Autowired
	private WeChatProperties weChatProperties;

	//openid的操作
	@Autowired
	private UserMapper userMapper;

	/*
	* 微信登录
	* */
	public User wxLogin(UserLoginDTO userLoginDTO) {
		String openid = getOpenid(userLoginDTO.getCode());

		//判断openid是否为空，如果为空表示登录失败，抛出异常
		if (openid == null){
			throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
		}

		//判断当前用户是否为新用户
		//这里需要进行查询
		User user = userMapper.getByOpenid(openid);

		//如果是新用户，自动完成注册
		if (user == null){
			//自动注册
			user = User.builder()
					.openid(openid)
					.createTime(LocalDateTime.now())
					.build();
			//插入注册的对象
			userMapper.insert(user);
		}

		//返回这个用户对象
		return user;
	}

	/*
	* 调用微信接口服务，获取微信用户的openid
	* */
	//获取openid方法，用于简化代码
	private String getOpenid(String code) {
       //调用微信接口服务，获得当前微信用户的openid
		//map中封装了对应的请求参数
		Map<String, String> map = new HashMap<>();
		//使用一个map对属性进行注入操作
		map.put("appid",weChatProperties.getAppid());
		map.put("secret",weChatProperties.getSecret());
		map.put("js_code",code);
		map.put("grant_type","authorization_code");
		//上面的所有类型封装完毕之后，发出请求
		String json = HttpClientUtil.doGet(WX_LOGIN, map);

		//获取封装之后传回来的值
		JSONObject jsonObject = JSON.parseObject(json);
		String openid = jsonObject.getString("openid");
		return openid;
	}
}