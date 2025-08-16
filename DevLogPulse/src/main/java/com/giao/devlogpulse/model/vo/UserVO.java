package com.giao.devlogpulse.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户VO
 */
@Data
@Accessors(chain = true)
public class UserVO {
    
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
     * 状态(1启用 0禁用)
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusText;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}