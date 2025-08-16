package com.giao.devlogpulse.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SA-Token配置类
 * 
 * @author giao
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 配置SA-Token的JWT模式
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

    /**
     * 注册SA-Token拦截器，打开注解式鉴权功能
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 登录校验 -- 拦截所有路由，并排除登录相关的路由
            cn.dev33.satoken.stp.StpUtil.checkLogin();
        }))
        .addPathPatterns("/**")
        .excludePathPatterns(
            // 排除登录相关接口
            "/auth/login",
            "/auth/register",
            "/auth/logout",
            // 排除静态资源
            "/static/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/*.ico",
            // 排除Swagger文档
            "/doc.html",
            "/webjars/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/favicon.ico",
            // 排除WebSocket
            "/websocket/**",
            // 排除系统测试接口
            "/system-test/**"
        );
    }
}