package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
	//根据openid来查询对应数据的操作
	@Select("select * from user where openid = #{openid}")
	User getByOpenid(String openid);

	/*
	* 用于新注册用户的操作，插入用户的数据
	* 这里可能用到动态sql，因此使用xml文件进行编写
	* */
	void insert(User user);

	/*
	* 获取用户的id
	* */
	@Select("select * from user where id = #{id}")
	User getById(Long userId);
}
