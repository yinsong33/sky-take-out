package com.sky.handler;

import com.sky.constant.MessageConstant;
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


    //接下来就是在这里处理异常信息，就是假如说在接口的阶段，我已经往里面添加了一个值了
    //然后我重复添加了，那么这个时候就是要给前端一个提醒，就是已经存在，或者不能添加，、
    //在全局异常里面处理，然后处理异常都必须要加注解就是@Excertionhandele


     @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){//这个是这个异常的对象ex
        //然后就是先是获取异常对象显示的数据，然后在里面看是否包含了键值对duplicate entry 'zhangsan001'
         String message = ex.getMessage();
         if(message.contains("Duplicate entry")){
             String[] s = message.split(" ");
             String s1 = s[2];//以空格来作为间隔，获取第三个值就是zhangsan001
             String msg=s1+ MessageConstant.ALREADY_EXISTS;
             return Result.error(msg);//直接就是如果包含，那么就直接返回错误给前端
             //也就是那个接口，告诉他提示信息，说已经存在.其实就是返回一个错误信息，
         }
         else {
             return Result.error("未知参数");
         }



     }





}
