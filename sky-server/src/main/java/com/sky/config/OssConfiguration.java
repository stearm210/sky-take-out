package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/*
* 配置类，主要用于创建aliossutil对象用于文件的上传
* */
@Configuration
@Slf4j
public class OssConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
		log.info("开始创建阿里云文件上传工具类对象:{}",aliOssProperties);
		//下面的操作是从配置文件中得到对应的属性值，并且使用new创建对象的方法进行对应的赋值配置
		return new AliOssUtil(aliOssProperties.getEndpoint(),aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret(), aliOssProperties.getBucketName());
	}
}