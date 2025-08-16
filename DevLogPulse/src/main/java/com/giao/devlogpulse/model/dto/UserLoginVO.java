package com.giao.devlogpulse.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户登录响应VO
 */
@Data
@Accessors(chain = true)
public class UserLoginVO {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 部门ID
     */
    private Long departmentId;
    
    /**
     * 部门名称
     */
    private String departmentName;
    
    /**
     * 组ID
     */
    private Long groupId;
    
    /**
     * 组名称
     */
    private String groupName;
    
    /**
     * 权限编码列表
     */
    private List<String> permissions;
    
    /**
     * 访问令牌
     */
    private String token;
    
    /**
     * token过期时间
     */
    private LocalDateTime tokenExpireTime;
} 