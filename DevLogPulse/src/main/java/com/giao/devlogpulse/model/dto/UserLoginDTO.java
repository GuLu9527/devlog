package com.giao.devlogpulse.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户登录请求DTO
 */
@Data
public class UserLoginDTO {
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
} 