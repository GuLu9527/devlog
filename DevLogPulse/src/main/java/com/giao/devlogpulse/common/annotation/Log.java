package com.giao.devlogpulse.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    
    /**
     * 模块名称
     */
    String module() default "";
    
    /**
     * 操作类型
     */
    String type() default "";
    
    /**
     * 操作描述
     */
    String description() default "";
    
    /**
     * 是否保存请求参数
     */
    boolean saveParams() default true;
    
    /**
     * 是否保存返回结果
     */
    boolean saveResult() default true;
} 