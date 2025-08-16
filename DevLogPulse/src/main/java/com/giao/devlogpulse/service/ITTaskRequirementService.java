package com.giao.devlogpulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.giao.devlogpulse.entity.po.TTaskRequirement;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 任务技能需求关联表 服务类
 * </p>
 *
 * @author giao
 * @since 2025-07-30
 */
public interface ITTaskRequirementService extends IService<TTaskRequirement> {
    
    /**
     * 获取任务技能需求列表（包含标签信息）
     */
    List<Map<String, Object>> getTaskRequirementsWithTags(Long taskId);
    
    /**
     * 根据标签查询需要该技能的任务
     */
    List<Map<String, Object>> getTasksBySkillTag(Long tagId);
    
    /**
     * 更新任务技能需求
     */
    boolean updateTaskRequirements(Long taskId, List<TTaskRequirement> requirements);
    
    /**
     * 添加任务技能需求
     */
    boolean addTaskRequirement(TTaskRequirement requirement);
    
    /**
     * 删除任务技能需求
     */
    boolean removeTaskRequirement(Long taskId, Long tagId);
    
    /**
     * 批量更新任务需求
     */
    boolean batchUpdateTaskRequirements(Long taskId, List<TTaskRequirement> requirements);
    
    /**
     * 根据用户技能匹配适合的任务
     */
    List<Map<String, Object>> findSuitableTasksForUser(Long userId);
    
    /**
     * 获取技能需求统计
     */
    List<Map<String, Object>> getSkillDemandStatistics();
    
    /**
     * 获取任务匹配度分析
     */
    Map<String, Object> getTaskMatchAnalysis(Long taskId, Long userId);
    
    /**
     * 推荐任务给用户
     */
    List<Map<String, Object>> recommendTasksForUser(Long userId, Integer limit);
    
    /**
     * 获取项目技能需求概览
     */
    Map<String, Object> getProjectSkillOverview(Long projectId);
}