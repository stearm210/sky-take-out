package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/*
*自定义的切面类，实现公共字段自定义填充逻辑
* */
/*
* 使用自定义的切面类，主要是为了公共属性的赋值操作，这样可以将原本需要多次操作的“记录操作时间、记录操作人”等统一通过一个注解注入的方式进行简化
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

		//一般来说是通过连接点获得对应的参数
		//获取当前被拦截的方法的数据库操作类型
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
		AutoFill autoFill=signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
		OperationType operationType=autoFill.value();//获得数据库操作类型(是update还是insert)


		//获取到当前被拦截的方法的参数--实体对象
		Object[] args = joinPoint.getArgs();
		if (args == null || args.length == 0){
			return;
		}

		//这里得到了对应的实体对象
		Object entity=args[0];

		//准备赋值的数据
		LocalDateTime now=LocalDateTime.now();
		Long currentId= BaseContext.getCurrentId();//获得对应的操作id

		//根据当前不同的操作类型，为对应的属性通过反射来进行赋值
		//如果是插入操作的话，需要进行怎样的操作
		if (operationType == OperationType.INSERT){
			//如果是插入操作，则需要为4个公共字段进行赋值
			try {
				//下面的操作对公共属性进行了更加快速的定义操作
				Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
				Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
				Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
				Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

				//通过反射为对象属性赋值
				//对公共属性进行对应的赋值
				setCreateTime.invoke(entity,now);
				setCreateUser.invoke(entity,currentId);
				setUpdateTime.invoke(entity,now);
				setUpdateUser.invoke(entity,currentId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (operationType == OperationType.UPDATE) {
			//如果是更新操作，则需要对两个字段进行赋值
			//如果是插入操作，则需要为4个公共字段进行赋值
			try {
				Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
				Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

				//通过反射为对象属性赋值
				//对公共属性进行对应的赋值
				setUpdateTime.invoke(entity,now);
				setUpdateUser.invoke(entity,currentId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}