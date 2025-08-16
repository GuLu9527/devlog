package com.giao.devlogpulse.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 用户创建DTO
 */
@Data
public class UserCreateDTO {
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "用户名只能包含字母、数字、下划线和中划线")
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
    
    /**
     * 真实姓名
     */
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;
    
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
    
    /**
     * 部门ID
     */
    private Long departmentId;
    
    /**
     * 组ID
     */
    private Long groupId;
    
    /**
     * 状态(1启用 0禁用)
     */
    private Integer status = 1;
}