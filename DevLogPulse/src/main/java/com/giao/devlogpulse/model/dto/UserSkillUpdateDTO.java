package com.giao.devlogpulse.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * 用户技能更新DTO
 */
@Data
public class UserSkillUpdateDTO {
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 技能列表
     */
    @NotNull(message = "技能列表不能为空")
    private List<UserSkillItemDTO> skills;
    
    /**
     * 用户技能项DTO
     */
    @Data
    public static class UserSkillItemDTO {
        
        /**
         * 标签ID
         */
        @NotNull(message = "标签ID不能为空")
        private Long tagId;
        
        /**
         * 熟练程度
         */
        @NotBlank(message = "熟练程度不能为空")
        @Pattern(regexp = "^(初级|中级|高级|专家)$", message = "熟练程度只能是：初级、中级、高级、专家")
        private String proficiencyLevel;
        
        /**
         * 使用年限
         */
        @Min(value = 0, message = "使用年限不能小于0")
        @Max(value = 50, message = "使用年限不能大于50")
        private Integer yearsOfExperience;
        
        /**
         * 最后使用时间
         */
        private LocalDate lastUsed;
        
        /**
         * 是否主要技能(1是 0否)
         */
        private Integer isPrimary = 0;
        
        /**
         * 自评分数(1-10分)
         */
        @Min(value = 1, message = "自评分数最小为1分")
        @Max(value = 10, message = "自评分数最大为10分")
        private Integer selfRating = 5;
        
        /**
         * 备注
         */
        @Size(max = 500, message = "备注长度不能超过500个字符")
        private String remark;
    }
}