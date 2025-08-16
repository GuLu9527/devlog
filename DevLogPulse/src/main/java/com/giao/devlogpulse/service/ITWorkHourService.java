package com.giao.devlogpulse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.giao.devlogpulse.entity.po.TWorkHour;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 工时记录服务接口
 */
public interface ITWorkHourService extends IService<TWorkHour> {

    /**
     * 创建工时记录
     */
    boolean createWorkHour(TWorkHour workHour);

    /**
     * 获取用户工时记录
     */
    Page<TWorkHour> getUserWorkHours(Long userId, LocalDate startDate, LocalDate endDate, String status, Integer pageNum, Integer pageSize);

    /**
     * 获取项目工时统计
     */
    Map<String, Object> getProjectWorkHourStats(Long projectId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取任务工时统计
     */
    Map<String, Object> getTaskWorkHourStats(Long taskId);

    /**
     * 审核工时记录
     */
    boolean reviewWorkHour(Long workHourId, TWorkHour.Status status, String reviewNote, Long reviewerId);

    /**
     * 获取待审核工时记录
     */
    Page<TWorkHour> getPendingWorkHours(Integer pageNum, Integer pageSize);

    /**
     * 批量审核工时记录
     */
    boolean batchReviewWorkHours(List<Long> workHourIds, TWorkHour.Status status, String reviewNote, Long reviewerId);

    /**
     * 获取用户工时统计
     */
    Map<String, Object> getUserWorkHourStats(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取团队工时排行
     */
    List<Map<String, Object>> getTeamWorkHourRanking(LocalDate startDate, LocalDate endDate, int limit);

    /**
     * 检查工时记录冲突
     */
    boolean checkWorkHourConflict(Long userId, Long taskId, LocalDate workDate);

    /**
     * 获取用户日工时汇总
     */
    BigDecimal getUserDailyWorkHours(Long userId, LocalDate date);
}