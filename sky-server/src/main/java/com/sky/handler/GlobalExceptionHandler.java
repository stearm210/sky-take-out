package com.sky.handler;

import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /*
    * 主要用于处理sql异常
    * */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //例子：Duxxxxx enxxx 'zhangsan' for key xxxx
        //1.先获取异常信息
        String message = ex.getMessage();
        //2.判断有没有关键字
        if (message.contains("Duplicate entry")){
            //根据空格进行分割
            String[] split = message.split(" ");
            //希望通过报错信息获得对应名字
            String username = split[2];
            String msg=username+"已存在";
            return Result.error(msg);
        }
    }
}
