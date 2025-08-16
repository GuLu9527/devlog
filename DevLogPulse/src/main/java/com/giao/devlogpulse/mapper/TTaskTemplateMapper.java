package com.giao.devlogpulse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.giao.devlogpulse.entity.po.TTaskTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 任务模板 Mapper 接口
 */
@Mapper
public interface TTaskTemplateMapper extends BaseMapper<TTaskTemplate> {

    /**
     * 增加模板使用次数
     */
    @Update("UPDATE t_task_template SET usage_count = usage_count + 1, last_used = NOW() WHERE id = #{templateId}")
    int incrementUsageCount(@Param("templateId") Long templateId);
}