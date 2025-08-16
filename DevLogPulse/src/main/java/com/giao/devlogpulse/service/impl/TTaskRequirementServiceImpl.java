package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giao.devlogpulse.entity.po.TTaskRequirement;
import com.giao.devlogpulse.mapper.TTaskRequirementMapper;
import com.giao.devlogpulse.service.ITTaskRequirementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>
 * 任务技能需求关联表 服务实现类
 * </p>
 *
 * @author giao
 * @since 2025-07-30
 */
@Service
public class TTaskRequirementServiceImpl extends ServiceImpl<TTaskRequirementMapper, TTaskRequirement> implements ITTaskRequirementService {

    @Override
    public List<Map<String, Object>> getTaskRequirementsWithTags(Long taskId) {
        return baseMapper.selectTaskRequirementsWithTags(taskId);
    }

    @Override
    public List<Map<String, Object>> getTasksBySkillTag(Long tagId) {
        return baseMapper.selectTasksBySkillTag(tagId);
    }

    @Override
    @Transactional
    public boolean updateTaskRequirements(Long taskId, List<TTaskRequirement> requirements) {
        // 先删除任务现有的所有需求
        baseMapper.deleteByTaskId(taskId);
        
        // 批量插入新的需求
        if (requirements != null && !requirements.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (TTaskRequirement requirement : requirements) {
                requirement.setTaskId(taskId);
                requirement.setCreateTime(now);
                requirement.setUpdateTime(now);
                if (requirement.getImportance() == null) {
                    requirement.setImportance(3);
                }
                if (requirement.getIsRequired() == null) {
                    requirement.setIsRequired(1);
                }
            }
            baseMapper.insertBatch(requirements);
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean addTaskRequirement(TTaskRequirement requirement) {
        // 检查是否已存在
        QueryWrapper<TTaskRequirement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", requirement.getTaskId())
                   .eq("required_tag_id", requirement.getRequiredTagId());
        TTaskRequirement existing = getOne(queryWrapper);
        
        if (existing != null) {
            // 更新现有记录
            existing.setMinimumProficiency(requirement.getMinimumProficiency());
            existing.setImportance(requirement.getImportance());
            existing.setIsRequired(requirement.getIsRequired());
            existing.setRemark(requirement.getRemark());
            existing.setUpdateTime(LocalDateTime.now());
            return updateById(existing);
        } else {
            // 新增记录
            requirement.setCreateTime(LocalDateTime.now());
            requirement.setUpdateTime(LocalDateTime.now());
            if (requirement.getImportance() == null) {
                requirement.setImportance(3);
            }
            if (requirement.getIsRequired() == null) {
                requirement.setIsRequired(1);
            }
            return save(requirement);
        }
    }

    @Override
    @Transactional
    public boolean removeTaskRequirement(Long taskId, Long tagId) {
        QueryWrapper<TTaskRequirement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId).eq("required_tag_id", tagId);
        return remove(queryWrapper);
    }

    @Override
    @Transactional
    public boolean batchUpdateTaskRequirements(Long taskId, List<TTaskRequirement> requirements) {
        return updateTaskRequirements(taskId, requirements);
    }

    @Override
    public List<Map<String, Object>> findSuitableTasksForUser(Long userId) {
        return baseMapper.findSuitableTasksForUser(userId);
    }

    @Override
    public List<Map<String, Object>> getSkillDemandStatistics() {
        return baseMapper.getSkillDemandStatistics();
    }

    @Override
    public Map<String, Object> getTaskMatchAnalysis(Long taskId, Long userId) {
        // TODO: 实现任务匹配度分析
        Map<String, Object> analysis = new HashMap<>();
        
        // 获取任务需求
        List<Map<String, Object>> taskRequirements = getTaskRequirementsWithTags(taskId);
        
        // 获取用户技能 - 这里需要调用用户技能服务
        // List<Map<String, Object>> userSkills = userSkillService.getUserSkillsWithTags(userId);
        
        // 计算匹配度
        // ...
        
        return analysis;
    }

    @Override
    public List<Map<String, Object>> recommendTasksForUser(Long userId, Integer limit) {
        List<Map<String, Object>> suitableTasks = findSuitableTasksForUser(userId);
        if (limit != null && limit > 0 && suitableTasks.size() > limit) {
            return suitableTasks.subList(0, limit);
        }
        return suitableTasks;
    }

    @Override
    public Map<String, Object> getProjectSkillOverview(Long projectId) {
        // TODO: 实现项目技能需求概览
        // 需要关联项目表和任务表
        return new HashMap<>();
    }
}