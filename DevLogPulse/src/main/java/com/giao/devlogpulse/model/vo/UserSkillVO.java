package com.giao.devlogpulse.model.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户技能VO
 */
@Data
public class UserSkillVO {
    
    /**
     * 关联ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 真实姓名
     */
    private String realName;
    
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
     * 熟练程度
     */
    private String proficiencyLevel;
    
    /**
     * 使用年限
     */
    private Integer yearsOfExperience;
    
    /**
     * 最后使用时间
     */
    private LocalDate lastUsed;
    
    /**
     * 是否主要技能(1是 0否)
     */
    private Integer isPrimary;
    
    /**
     * 主要技能文字
     */
    private String isPrimaryText;
    
    /**
     * 自评分数(1-10分)
     */
    private Integer selfRating;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // 主要技能文字的getter方法
    public String getIsPrimaryText() {
        return isPrimary != null && isPrimary == 1 ? "是" : "否";
    }
}