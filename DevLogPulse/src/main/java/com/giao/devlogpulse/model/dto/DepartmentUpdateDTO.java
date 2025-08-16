package com.giao.devlogpulse.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 部门更新DTO
 */
@Data
public class DepartmentUpdateDTO {
    
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
    @NotNull(message = "状态不能为空")
    private Integer status;
}