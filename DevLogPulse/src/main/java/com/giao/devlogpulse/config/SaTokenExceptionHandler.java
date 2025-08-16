package com.giao.devlogpulse.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.common.api.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * SA-Token异常处理器
 * 
 * @author giao
 */
@RestControllerAdvice
@Slf4j
@Order(1) // 确保这个处理器优先级高于全局异常处理器
public class SaTokenExceptionHandler {

    /**
     * 处理未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<Object> handleNotLoginException(NotLoginException e) {
        log.warn("SA-Token未登录异常: {}", e.getMessage());
        
        // 根据不同的未登录类型返回不同的提示
        String message;
        switch (e.getType()) {
            case NotLoginException.NOT_TOKEN:
                message = "未提供认证令牌";
                break;
            case NotLoginException.INVALID_TOKEN:
                message = "认证令牌无效";
                break;
            case NotLoginException.TOKEN_TIMEOUT:
                message = "认证令牌已过期";
                break;
            case NotLoginException.BE_REPLACED:
                message = "认证令牌已被替换";
                break;
            case NotLoginException.KICK_OUT:
                message = "您已被踢下线";
                break;
            default:
                message = "请先登录";
                break;
        }
        
        return new Result<Object>().setCode(ResultCode.UNAUTHORIZED.getCode()).setMessage(message);
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public Result<Object> handleNotPermissionException(NotPermissionException e) {
        log.warn("SA-Token权限不足异常: {}", e.getMessage());
        return new Result<Object>().setCode(ResultCode.FORBIDDEN.getCode()).setMessage("权限不足：" + e.getPermission());
    }

    /**
     * 处理角色不足异常
     */
    @ExceptionHandler(NotRoleException.class)
    public Result<Object> handleNotRoleException(NotRoleException e) {
        log.warn("SA-Token角色不足异常: {}", e.getMessage());
        return new Result<Object>().setCode(ResultCode.FORBIDDEN.getCode()).setMessage("角色权限不足：" + e.getRole());
    }
}