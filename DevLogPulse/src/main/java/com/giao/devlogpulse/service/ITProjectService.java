package com.giao.devlogpulse.service;

import com.giao.devlogpulse.entity.po.TProject;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 项目表 服务类
 * </p>
 *
 * @author giao
 * @since 2025-05-22
 */
public interface ITProjectService extends IService<TProject> {
    
    /**
     * 计算项目整体进度
     * 基于所有子任务的完成情况计算项目进度
     *
     * @param projectId 项目ID
     * @return 项目进度信息，包含进度百分比、总任务数、已完成任务数、总工时、已完成工时等
     */
    Map<String, Object> calculateProjectProgress(Long projectId);
    
    /**
     * 更新项目进度（当任务状态改变时触发）
     * 
     * @param projectId 项目ID
     * @return 是否更新成功
     */
    boolean updateProjectProgress(Long projectId);
    
    /**
     * 获取项目统计信息
     * 
     * @param projectId 项目ID
     * @return 统计信息
     */
    Map<String, Object> getProjectStatistics(Long projectId);
}
