package com.giao.devlogpulse.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

/**
 * 任务技能需求更新DTO
 */
@Data
public class TaskRequirementUpdateDTO {
    
    /**
     * 任务ID
     */
    @NotNull(message = "任务ID不能为空")
    private Long taskId;
    
    /**
     * 技能需求列表
     */
    @NotNull(message = "技能需求列表不能为空")
    private List<TaskRequirementItemDTO> requirements;
    
    /**
     * 任务技能需求项DTO
     */
    @Data
    public static class TaskRequirementItemDTO {
        
        /**
         * 所需标签ID
         */
        @NotNull(message = "标签ID不能为空")
        private Long requiredTagId;
        
        /**
         * 最低熟练程度要求
         */
        @NotBlank(message = "最低熟练程度不能为空")
        @Pattern(regexp = "^(初级|中级|高级|专家)$", message = "熟练程度只能是：初级、中级、高级、专家")
        private String minimumProficiency;
        
        /**
         * 重要程度(1-5级，5最重要)
         */
        @Min(value = 1, message = "重要程度最小为1级")
        @Max(value = 5, message = "重要程度最大为5级")
        private Integer importance = 3;
        
        /**
         * 是否必需(1必需 0可选)
         */
        private Integer isRequired = 1;
        
        /**
         * 备注说明
         */
        @Size(max = 500, message = "备注长度不能超过500个字符")
        private String remark;
    }
}