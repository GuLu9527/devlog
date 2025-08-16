package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giao.devlogpulse.entity.po.TTask;
import com.giao.devlogpulse.entity.po.TTaskDependency;
import com.giao.devlogpulse.mapper.TTaskDependencyMapper;
import com.giao.devlogpulse.service.ITTaskDependencyService;
import com.giao.devlogpulse.service.ITTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务依赖关系服务实现类
 */
@Service
public class TTaskDependencyServiceImpl extends ServiceImpl<TTaskDependencyMapper, TTaskDependency> implements ITTaskDependencyService {

    @Autowired
    private ITTaskService taskService;

    @Override
    public Map<String, Object> getTaskDependencies(Long taskId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取当前任务依赖的任务（前置依赖）
        List<TTaskDependency> dependsOn = baseMapper.getDependentTasks(taskId);
        List<Map<String, Object>> dependsOnList = dependsOn.stream().map(dependency -> {
            TTask task = taskService.getById(dependency.getDependentTaskId());
            Map<String, Object> taskInfo = new HashMap<>();
            taskInfo.put("id", task.getId());
            taskInfo.put("title", task.getTitle());
            taskInfo.put("status", task.getStatus());
            taskInfo.put("progress", task.getProgress());
            return taskInfo;
        }).collect(Collectors.toList());
        
        // 获取依赖当前任务的任务（后续任务）
        List<TTaskDependency> dependents = baseMapper.getBlockingTasks(taskId);
        List<Map<String, Object>> dependentsList = dependents.stream().map(dependency -> {
            TTask task = taskService.getById(dependency.getTaskId());
            Map<String, Object> taskInfo = new HashMap<>();
            taskInfo.put("id", task.getId());
            taskInfo.put("title", task.getTitle());
            taskInfo.put("status", task.getStatus());
            taskInfo.put("progress", task.getProgress());
            return taskInfo;
        }).collect(Collectors.toList());
        
        result.put("dependsOn", dependsOnList);
        result.put("dependents", dependentsList);
        
        return result;
    }

    @Override
    @Transactional
    public boolean addTaskDependency(Long taskId, Long dependentTaskId, TTaskDependency.DependencyType type, String description, Long creatorId) {
        // 检查循环依赖
        if (checkCircularDependency(taskId, dependentTaskId)) {
            throw new RuntimeException("存在循环依赖，无法添加依赖关系");
        }
        
        // 检查是否已存在该依赖关系
        LambdaQueryWrapper<TTaskDependency> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TTaskDependency::getTaskId, taskId)
                   .eq(TTaskDependency::getDependentTaskId, dependentTaskId)
                   .eq(TTaskDependency::getIsDeleted, 0);
        
        if (count(queryWrapper) > 0) {
            throw new RuntimeException("依赖关系已存在");
        }
        
        TTaskDependency dependency = new TTaskDependency();
        dependency.setTaskId(taskId);
        dependency.setDependentTaskId(dependentTaskId);
        dependency.setType(type);
        dependency.setDescription(description);
        dependency.setCreatorId(creatorId);
        dependency.setCreateTime(LocalDateTime.now());
        dependency.setUpdateTime(LocalDateTime.now());
        dependency.setIsDeleted(0);
        
        return save(dependency);
    }

    @Override
    @Transactional
    public boolean removeTaskDependency(Long taskId, Long dependentTaskId) {
        LambdaQueryWrapper<TTaskDependency> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TTaskDependency::getTaskId, taskId)
                   .eq(TTaskDependency::getDependentTaskId, dependentTaskId)
                   .eq(TTaskDependency::getIsDeleted, 0);
        
        TTaskDependency dependency = getOne(queryWrapper);
        if (dependency != null) {
            dependency.setIsDeleted(1);
            dependency.setUpdateTime(LocalDateTime.now());
            return updateById(dependency);
        }
        
        return false;
    }

    @Override
    public boolean checkCircularDependency(Long taskId, Long dependentTaskId) {
        if (taskId.equals(dependentTaskId)) {
            return true;
        }
        
        int count = baseMapper.checkCircularDependency(taskId, dependentTaskId);
        return count > 0;
    }

    @Override
    public List<TTaskDependency> getTaskDependencyPath(Long taskId) {
        LambdaQueryWrapper<TTaskDependency> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TTaskDependency::getTaskId, taskId)
                   .eq(TTaskDependency::getIsDeleted, 0);
        
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public boolean removeTaskDependenciesByTaskId(Long taskId) {
        LambdaQueryWrapper<TTaskDependency> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(TTaskDependency::getTaskId, taskId)
                                          .or()
                                          .eq(TTaskDependency::getDependentTaskId, taskId))
                   .eq(TTaskDependency::getIsDeleted, 0);
        
        List<TTaskDependency> dependencies = list(queryWrapper);
        if (!dependencies.isEmpty()) {
            dependencies.forEach(dependency -> {
                dependency.setIsDeleted(1);
                dependency.setUpdateTime(LocalDateTime.now());
            });
            return updateBatchById(dependencies);
        }
        
        return true;
    }
}