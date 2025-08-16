package com.giao.devlogpulse.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 重置密码请求DTO
 */
@Data
public class ResetPasswordDTO {
    
    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$", 
            message = "密码必须包含大小写字母和数字")
    private String newPassword;
} 