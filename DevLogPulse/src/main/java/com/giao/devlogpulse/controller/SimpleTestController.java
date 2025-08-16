package com.giao.devlogpulse.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.giao.devlogpulse.common.api.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单测试控制器 - 用于测试权限切面
 * 
 * @author giao
 */
@RestController
@RequestMapping("/simple")
@Slf4j
public class SimpleTestController {

    /**
     * 无权限要求的接口
     */
    @GetMapping("/no-auth")
    public Result<Map<String, Object>> noAuth(HttpServletRequest request) {
        log.info("=== 无权限接口被调用 ===");
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "无权限接口调用成功");
        result.put("timestamp", System.currentTimeMillis());
        
        // 检查request中的属性
        Object userId = request.getAttribute("userId");
        Object username = request.getAttribute("username");
        
        result.put("userId", userId);
        result.put("username", username);
        
        return Result.success(result);
    }

    /**
     * 需要权限的接口
     */
    @GetMapping("/with-auth")
    @SaCheckPermission("user:list")
    public Result<Map<String, Object>> withAuth(HttpServletRequest request) {
        log.info("=== 需要权限的接口被调用 - 如果看到这条日志说明权限验证通过了 ===");
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "权限验证通过！你有 user:list 权限");
        result.put("timestamp", System.currentTimeMillis());
        
        Object userId = request.getAttribute("userId");
        Object username = request.getAttribute("username");
        
        result.put("userId", userId);
        result.put("username", username);
        
        return Result.success(result);
    }
}