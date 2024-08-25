package com.sky.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/*
*自定义的切面类，实现公共字段自定义填充逻辑
* */
//切面=切入点+通知
@Aspect
//加入aop容器
@Component
//lombok中进行操作
@Slf4j
public class AutoFillAspect {
	//切入点
	/*
	* 对哪些类、哪些方法进行拦截
	* */
	//拦截对应包下的类与方法,并且满足加上了AutoFill注解的类
	@Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
	public void autoFillPointCut(){

	}

	//前置通知，在通知中进行公共字段的赋值
	/*
	* 前置通知，在通知中进行公共字段的赋值
	* */
	@Before("autoFillPointCut()")
	public void autoFill(JoinPoint joinPoint){
		//公共字段的自动填充
		log.info("开始进行公共字段自动填充");
	}

}