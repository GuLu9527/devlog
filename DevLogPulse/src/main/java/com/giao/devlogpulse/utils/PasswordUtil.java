package com.giao.devlogpulse.utils;

import cn.hutool.crypto.digest.BCrypt;
import org.springframework.stereotype.Component;

/**
 * 密码加密工具类
 */
@Component
public class PasswordUtil {
    
    /**
     * 加密密码
     */
    public String encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    /**
     * 验证密码
     */
    public boolean matches(String password, String encodedPassword) {
        return BCrypt.checkpw(password, encodedPassword);
    }
}