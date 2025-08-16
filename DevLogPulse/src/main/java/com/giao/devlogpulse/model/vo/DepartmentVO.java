package com.giao.devlogpulse.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门VO
 */
@Data
@Accessors(chain = true)
public class DepartmentVO {
    
    /**
     * 部门ID
     */
    private Long id;
    
    /**
     * 部门名称
     */
    private String name;
    
    /**
     * 父部门ID
     */
    private Long parentId;
    
    /**
     * 父部门名称
     */
    private String parentName;
    
    /**
     * 状态(1启用 0禁用)
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusText;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 子部门列表（树形结构使用）
     */
    private List<DepartmentVO> children;
    
    /**
     * 用户数量
     */
    private Integer userCount;
}