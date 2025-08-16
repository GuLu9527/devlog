package com.giao.devlogpulse.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限控制注解
 * 用于Service方法上，进行数据范围控制
 * 
 * @author giao
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {
    
    /**
     * 数据权限类型
     */
    DataScopeType value() default DataScopeType.CURRENT_USER;
    
    /**
     * 数据权限字段名（默认user_id）
     */
    String userColumn() default "user_id";
    
    /**
     * 部门权限字段名（默认department_id）
     */
    String deptColumn() default "department_id";
    
    /**
     * 数据权限类型枚举
     */
    enum DataScopeType {
        ALL,            // 全部数据权限
        CURRENT_DEPT,   // 当前部门数据权限
        CURRENT_GROUP,  // 当前小组数据权限
        CURRENT_USER,   // 当前用户数据权限
        CUSTOM          // 自定义数据权限
    }
}