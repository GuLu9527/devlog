package com.giao.devlogpulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.giao.devlogpulse.entity.po.TTaskTemplate;

import java.util.List;

/**
 * 任务模板服务接口
 */
public interface ITTaskTemplateService extends IService<TTaskTemplate> {

    /**
     * 创建任务模板
     */
    boolean createTemplate(TTaskTemplate template);

    /**
     * 应用任务模板
     */
    List<Long> applyTemplate(Long templateId, Long projectId, Long assigneeId, String startDate, String taskPrefix);

    /**
     * 克隆任务模板
     */
    TTaskTemplate cloneTemplate(Long templateId, String newName);

    /**
     * 按分类获取模板
     */
    List<TTaskTemplate> getTemplatesByCategory(String category);

    /**
     * 增加模板使用次数
     */
    boolean incrementUsageCount(Long templateId);

    /**
     * 导入任务模板
     */
    boolean importTemplate(TTaskTemplate template);

    /**
     * 获取热门模板
     */
    List<TTaskTemplate> getPopularTemplates(int limit);
}