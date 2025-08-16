package com.giao.devlogpulse.service;

import java.time.LocalDate;
import java.util.Map;

/**
 * 统计分析服务接口
 */
public interface IStatisticsService {

    /**
     * 获取工时统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param userId 用户ID
     * @param groupId 组ID
     * @param projectId 项目ID
     * @param dimension 统计维度 (day/week/month)
     * @return 工时统计数据
     */
    Map<String, Object> getWorkHoursStatistics(LocalDate startDate, LocalDate endDate, Long userId, Long groupId, Long projectId, String dimension);

    /**
     * 获取任务统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param userId 用户ID
     * @param groupId 组ID
     * @param projectId 项目ID
     * @return 任务统计数据
     */
    Map<String, Object> getTaskStatistics(LocalDate startDate, LocalDate endDate, Long userId, Long groupId, Long projectId);

    /**
     * 获取项目进度统计数据
     * @param projectId 项目ID
     * @param departmentId 部门ID
     * @return 项目统计数据
     */
    Map<String, Object> getProjectStatistics(Long projectId, Long departmentId);

    /**
     * 获取个人工作概览
     * @param userId 用户ID
     * @return 个人工作概览数据
     */
    Map<String, Object> getPersonalOverview(Long userId);

    /**
     * 获取团队工作概览
     * @param groupId 组ID
     * @param departmentId 部门ID
     * @return 团队工作概览数据
     */
    Map<String, Object> getTeamOverview(Long groupId, Long departmentId);

    /**
     * 获取首页仪表板数据
     * @return 仪表板数据
     */
    Map<String, Object> getDashboardData();
}