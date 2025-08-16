package com.giao.devlogpulse.common.api;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 通用响应结果类
 */
@Data
@Accessors(chain = true)
public class Result<T> {
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 成功
     */
    public static <T> Result<T> success() {
        return new Result<T>().setCode(ResultCode.SUCCESS.getCode())
                .setMessage(ResultCode.SUCCESS.getMessage());
    }
    
    /**
     * 成功，带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>().setCode(ResultCode.SUCCESS.getCode())
                .setMessage(ResultCode.SUCCESS.getMessage())
                .setData(data);
    }
    
    /**
     * 失败
     */
    public static <T> Result<T> failed() {
        return new Result<T>().setCode(ResultCode.FAILED.getCode())
                .setMessage(ResultCode.FAILED.getMessage());
    }
    
    /**
     * 失败，自定义消息
     */
    public static <T> Result<T> failed(String message) {
        return new Result<T>().setCode(ResultCode.FAILED.getCode())
                .setMessage(message);
    }
    
    /**
     * 失败，自定义错误码和消息
     */
    public static <T> Result<T> failed(IErrorCode errorCode) {
        return new Result<T>().setCode(errorCode.getCode())
                .setMessage(errorCode.getMessage());
    }
    
    /**
     * 参数验证失败
     */
    public static <T> Result<T> validateFailed() {
        return new Result<T>().setCode(ResultCode.VALIDATE_FAILED.getCode())
                .setMessage(ResultCode.VALIDATE_FAILED.getMessage());
    }
    
    /**
     * 参数验证失败，自定义消息
     */
    public static <T> Result<T> validateFailed(String message) {
        return new Result<T>().setCode(ResultCode.VALIDATE_FAILED.getCode())
                .setMessage(message);
    }
    
    /**
     * 未登录
     */
    public static <T> Result<T> unauthorized() {
        return new Result<T>().setCode(ResultCode.UNAUTHORIZED.getCode())
                .setMessage(ResultCode.UNAUTHORIZED.getMessage());
    }
    
    /**
     * 未授权
     */
    public static <T> Result<T> forbidden() {
        return new Result<T>().setCode(ResultCode.FORBIDDEN.getCode())
                .setMessage(ResultCode.FORBIDDEN.getMessage());
    }
} 