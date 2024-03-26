package com.iron.config.exception;

import com.iron.config.exception.myException.IronException;
import com.iron.result.Result;
import com.iron.result.ResultCodeEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;




@RestControllerAdvice
public class GlobalException {

    // 全局异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result erroe(Exception e) {

        e.printStackTrace();
        return Result.fail().message("全局异常处理");
    }

    // 特定异常处理
    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public Result erro(ArithmeticException e) {

        e.printStackTrace();
        return Result.fail().message("特定异常 ArithMeticException触发");
    }

    // 自定义异常处理
    @ExceptionHandler(IronException.class)
    @ResponseBody
    public Result erro(IronException e) {

        e.printStackTrace();
        return Result.fail().message("特定异常 Iron异常触发");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result error(AccessDeniedException e) throws AccessDeniedException {
        return Result.build(null, ResultCodeEnum.PERMISSION);
    }

}
