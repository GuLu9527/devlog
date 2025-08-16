package com.giao.devlogpulse.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 部门创建DTO
 */
@Data
public class DepartmentCreateDTO {
    
    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称长度不能超过50个字符")
    private String name;
    
    /**
     * 父部门ID
     */
    private Long parentId;
    
    /**
     * 状态(1启用 0禁用)
     */
    private Integer status = 1;
}