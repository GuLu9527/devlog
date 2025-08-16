package com.giao.devlogpulse.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 技能标签创建DTO
 */
@Data
public class SkillTagCreateDTO {
    
    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称长度不能超过50个字符")
    private String tagName;
    
    /**
     * 标签描述
     */
    @Size(max = 255, message = "标签描述长度不能超过255个字符")
    private String description;
    
    /**
     * 标签颜色
     */
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "颜色格式不正确，应为#RRGGBB格式")
    private String color;
    
    /**
     * 标签分类
     */
    @NotBlank(message = "标签分类不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    private String category;
    
    /**
     * 排序权重
     */
    @Min(value = 0, message = "排序权重不能小于0")
    @Max(value = 999, message = "排序权重不能大于999")
    private Integer sortOrder = 0;
    
    /**
     * 状态(1启用 0禁用)
     */
    private Integer status = 1;
}