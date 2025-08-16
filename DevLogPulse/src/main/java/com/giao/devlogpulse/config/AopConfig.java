package com.giao.devlogpulse.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * AOP和异步配置类
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableAsync
@Slf4j
public class AopConfig {
    
    public AopConfig() {
        log.info("🚀 AOP和异步配置初始化完成！");
    }
}