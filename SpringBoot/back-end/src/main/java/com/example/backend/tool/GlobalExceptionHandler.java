package com.example.backend.tool;

import com.example.backend.entity.RestBean;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获所有的 RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public RestBean<String> handleRuntimeException(RuntimeException e) {
        // 打印异常到后台日志，方便调试
        e.printStackTrace();
        // 返回封装后的 JSON 给前端
        return RestBean.failure(500,e.getMessage());
    }

    /**
     * 捕获其他未知的系统异常 (如数据库连接断开)
     */
    @ExceptionHandler(Exception.class)
    public RestBean<String> handleException(Exception e) {
        e.printStackTrace();
        return RestBean.failure(500,"服务器开小差了，请稍后再试");
    }
}