package com.giao.devlogpulse.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 测试切面 - 验证AOP是否工作
 */
//@Aspect
//@Component
@Slf4j
public class TestAspect {
    
    public TestAspect() {
        log.info("🚀 TestAspect 初始化成功！");
    }

    /**
     * 拦截所有Controller方法
     */
//    @Around("execution(* com.giao.devlogpulse.controller.*.*(..))")
    public Object testAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        log.info("🔍 TestAspect 拦截到方法: {}.{}", className, methodName);
        
        try {
            Object result = joinPoint.proceed();
            log.info("✅ TestAspect 方法执行成功: {}.{}", className, methodName);
            return result;
        } catch (Exception e) {
            log.error("❌ TestAspect 方法执行失败: {}.{} - {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}