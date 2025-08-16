package com.giao.devlogpulse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.giao.devlogpulse.entity.po.TTaskDependency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 任务依赖关系 Mapper 接口
 */
@Mapper
public interface TTaskDependencyMapper extends BaseMapper<TTaskDependency> {

    /**
     * 获取任务的所有依赖任务
     */
    @Select("SELECT td.*, t.title as dependentTaskTitle, t.status as dependentTaskStatus, t.progress as dependentTaskProgress " +
            "FROM t_task_dependency td " +
            "LEFT JOIN t_task t ON td.dependent_task_id = t.id " +
            "WHERE td.task_id = #{taskId} AND td.is_deleted = 0 AND t.is_deleted = 0")
    List<TTaskDependency> getDependentTasks(@Param("taskId") Long taskId);

    /**
     * 获取依赖当前任务的任务
     */
    @Select("SELECT td.*, t.title as taskTitle, t.status as taskStatus, t.progress as taskProgress " +
            "FROM t_task_dependency td " +
            "LEFT JOIN t_task t ON td.task_id = t.id " +
            "WHERE td.dependent_task_id = #{taskId} AND td.is_deleted = 0 AND t.is_deleted = 0")
    List<TTaskDependency> getBlockingTasks(@Param("taskId") Long taskId);

    /**
     * 检查是否存在循环依赖
     */
    @Select("WITH RECURSIVE dependency_path AS ( " +
            "  SELECT task_id, dependent_task_id, 1 as level " +
            "  FROM t_task_dependency " +
            "  WHERE task_id = #{taskId} AND is_deleted = 0 " +
            "  UNION ALL " +
            "  SELECT td.task_id, td.dependent_task_id, dp.level + 1 " +
            "  FROM t_task_dependency td " +
            "  INNER JOIN dependency_path dp ON td.task_id = dp.dependent_task_id " +
            "  WHERE td.is_deleted = 0 AND dp.level < 10 " +
            ") " +
            "SELECT COUNT(*) FROM dependency_path WHERE dependent_task_id = #{dependentTaskId}")
    int checkCircularDependency(@Param("taskId") Long taskId, @Param("dependentTaskId") Long dependentTaskId);
}