package com.giao.devlogpulse.common.api;

import lombok.Getter;

/**
 * 通用错误码枚举
 */
@Getter
public enum ResultCode implements IErrorCode {
    
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    
    // 用户相关错误码
    USER_NOT_FOUND(1001, "用户不存在"),
    USERNAME_OR_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_DISABLED(1003, "用户已被禁用"),
    USERNAME_EXISTS(1004, "用户名已存在"),
    EMAIL_EXISTS(1005, "邮箱已存在"),
    PASSWORD_ERROR(1005, "密码错误"),
    PASSWORD_NOT_MATCH(1006, "两次输入的密码不一致"),
    PASSWORD_SAME(1007, "新密码不能与旧密码相同"),
    
    // 角色相关错误码
    ROLE_NOT_FOUND(2001, "角色不存在"),
    ROLE_NAME_EXISTS(2002, "角色名已存在"),
    
    // 部门相关错误码
    DEPARTMENT_NOT_FOUND(3001, "部门不存在"),
    DEPARTMENT_NAME_EXISTS(3002, "部门名已存在"),
    DEPARTMENT_HAS_USERS(3003, "部门下存在用户，无法删除"),
    
    // 权限相关错误码
    PERMISSION_NOT_FOUND(4001, "权限不存在"),
    PERMISSION_CODE_EXISTS(4002, "权限编码已存在"),
    PARAM_ERROR(4003,"类型错误" ),
    
    // 项目相关错误码
    PROJECT_NAME_EXISTS(5001, "项目名称已存在"),
    PROJECT_NOT_FOUND(5002, "项目不存在"),
    
    // 任务相关错误码
    TASK_NOT_FOUND(6001, "任务不存在"),
    TASK_NO_PERMISSION(6002, "无权操作此任务"),
    
    // 工作日志相关错误码
    WORKLOG_NOT_FOUND(7001, "工作日志不存在"),
    WORKLOG_NO_PERMISSION(7002, "无权操作此工作日志"),
    
    // 日志模板相关错误码
    LOG_TEMPLATE_NOT_FOUND(8001, "日志模板不存在"),
    
    // 小组相关错误码
    GROUP_NOT_FOUND(9001, "小组不存在"),
    GROUP_NAME_EXISTS(9002, "小组名称已存在"),
    GROUP_HAS_USERS(9003, "小组下存在用户，无法删除");
    
    private final Integer code;
    private final String message;
    
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
} 