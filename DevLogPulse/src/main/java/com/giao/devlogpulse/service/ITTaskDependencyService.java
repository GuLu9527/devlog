package com.giao.devlogpulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.giao.devlogpulse.entity.po.TTaskDependency;

import java.util.List;
import java.util.Map;

/**
 * 任务依赖关系服务接口
 */
public interface ITTaskDependencyService extends IService<TTaskDependency> {

    /**
     * 获取任务依赖关系
     */
    Map<String, Object> getTaskDependencies(Long taskId);

    /**
     * 添加任务依赖关系
     */
    boolean addTaskDependency(Long taskId, Long dependentTaskId, TTaskDependency.DependencyType type, String description, Long creatorId);

    /**
     * 删除任务依赖关系
     */
    boolean removeTaskDependency(Long taskId, Long dependentTaskId);

    /**
     * 检查循环依赖
     */
    boolean checkCircularDependency(Long taskId, Long dependentTaskId);

    /**
     * 获取任务的依赖路径
     */
    List<TTaskDependency> getTaskDependencyPath(Long taskId);

    /**
     * 批量删除任务依赖关系
     */
    boolean removeTaskDependenciesByTaskId(Long taskId);
}