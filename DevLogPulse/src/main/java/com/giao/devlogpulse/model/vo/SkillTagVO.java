package com.giao.devlogpulse.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 技能标签VO
 */
@Data
public class SkillTagVO {
    
    /**
     * 标签ID
     */
    private Long tagId;
    
    /**
     * 标签名称
     */
    private String tagName;
    
    /**
     * 标签描述
     */
    private String description;
    
    /**
     * 标签颜色
     */
    private String color;
    
    /**
     * 标签分类
     */
    private String category;
    
    /**
     * 排序权重
     */
    private Integer sortOrder;
    
    /**
     * 状态(1启用 0禁用)
     */
    private Integer status;
    
    /**
     * 状态文字
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
     * 使用统计
     */
    private Long userCount;  // 掌握该技能的用户数
    
    /**
     * 任务需求统计
     */
    private Long taskCount;  // 需要该技能的任务数
    
    // 状态文字的getter方法
    public String getStatusText() {
        return status != null && status == 1 ? "启用" : "禁用";
    }
}