package com.giao.devlogpulse.model.dto;

import lombok.Data;

/**
 * 用户分页查询DTO
 */
@Data
public class UserPageDTO {
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 页大小
     */
    private Integer pageSize = 10;
    
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
     * 状态(1启用 0禁用)
     */
    private Integer status;
    
    /**
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 部门ID
     */
    private Long departmentId;
    
    /**
     * 组ID
     */
    private Long groupId;
}