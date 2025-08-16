package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.giao.devlogpulse.entity.po.TTask;
import com.giao.devlogpulse.entity.po.TWorkLog;
import com.giao.devlogpulse.entity.po.TProject;
import com.giao.devlogpulse.entity.po.TUser;
import com.giao.devlogpulse.mapper.TTaskMapper;
import com.giao.devlogpulse.mapper.TWorkLogMapper;
import com.giao.devlogpulse.mapper.TProjectMapper;
import com.giao.devlogpulse.mapper.TUserMapper;
import com.giao.devlogpulse.service.IStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements IStatisticsService {

    private final TWorkLogMapper workLogMapper;
    private final TTaskMapper taskMapper;
    private final TProjectMapper projectMapper;
    private final TUserMapper userMapper;

    @Override
    public Map<String, Object> getWorkHoursStatistics(LocalDate startDate, LocalDate endDate, 
                                                     Long userId, Long groupId, Long projectId, String dimension) {
        Map<String, Object> result = new HashMap<>();

        // 设置默认时间范围（最近30天）
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // 构建查询条件
        QueryWrapper<TWorkLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0)
                   .eq("review_status", "已通过")
                   .between("log_date", startDate, endDate);

        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (groupId != null) {
            queryWrapper.eq("group_id", groupId);
        }

        List<TWorkLog> workLogs = workLogMapper.selectList(queryWrapper);

        // 如果指定了项目ID，需要过滤任务
        if (projectId != null) {
            Set<Long> projectTaskIds = getTaskIdsByProject(projectId);
            workLogs = workLogs.stream()
                    .filter(log -> projectTaskIds.contains(log.getTaskId()))
                    .collect(Collectors.toList());
        }

        // 总工时统计
        BigDecimal totalHours = workLogs.stream()
                .map(TWorkLog::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("totalHours", totalHours);

        // 按时间维度统计
        Map<String, BigDecimal> timeStatistics = calculateTimeStatistics(workLogs, dimension);
        result.put("timeStatistics", timeStatistics);

        // 按用户统计
        Map<Long, BigDecimal> userStatistics = workLogs.stream()
                .collect(Collectors.groupingBy(
                        TWorkLog::getUserId,
                        Collectors.reducing(BigDecimal.ZERO, TWorkLog::getHours, BigDecimal::add)
                ));
        result.put("userStatistics", userStatistics);

        // 按项目统计（如果没有指定具体项目）
        if (projectId == null) {
            Map<Long, BigDecimal> projectStatistics = calculateProjectStatistics(workLogs);
            result.put("projectStatistics", projectStatistics);
        }

        return result;
    }

    @Override
    public Map<String, Object> getTaskStatistics(LocalDate startDate, LocalDate endDate, 
                                                Long userId, Long groupId, Long projectId) {
        Map<String, Object> result = new HashMap<>();

        // 构建查询条件
        QueryWrapper<TTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);

        if (startDate != null && endDate != null) {
            queryWrapper.between("create_time", startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        }
        if (userId != null) {
            queryWrapper.eq("assignee_id", userId);
        }
        if (groupId != null) {
            queryWrapper.eq("group_id", groupId);
        }
        if (projectId != null) {
            queryWrapper.eq("project_id", projectId);
        }

        List<TTask> tasks = taskMapper.selectList(queryWrapper);

        // 任务总数
        result.put("totalTasks", tasks.size());

        // 按状态统计
        Map<String, Long> statusStatistics = tasks.stream()
                .collect(Collectors.groupingBy(
                    task -> task.getStatus() != null ? task.getStatus().name() : "未知状态", 
                    Collectors.counting()
                ));
        result.put("statusStatistics", statusStatistics);

        // 按优先级统计
        Map<String, Long> priorityStatistics = tasks.stream()
                .collect(Collectors.groupingBy(
                    task -> task.getPriority() != null ? task.getPriority().name() : "未知优先级", 
                    Collectors.counting()
                ));
        result.put("priorityStatistics", priorityStatistics);

        // 完成率统计
        long completedTasks = tasks.stream()
                .mapToLong(task -> task.getStatus() == TTask.Status.已完成 ? 1 : 0)
                .sum();
        double completionRate = tasks.isEmpty() ? 0 : (double) completedTasks / tasks.size() * 100;
        result.put("completionRate", completionRate);

        // 逾期任务统计
        long overdueTasks = tasks.stream()
                .filter(task -> task.getStatus() != TTask.Status.已完成 && task.getStatus() != TTask.Status.已取消)
                .filter(task -> task.getDeadline() != null && task.getDeadline().isBefore(LocalDateTime.now()))
                .count();
        result.put("overdueTasks", overdueTasks);

        // 逾期率
        double overdueRate = tasks.isEmpty() ? 0 : (double) overdueTasks / tasks.size() * 100;
        result.put("overdueRate", overdueRate);

        return result;
    }

    @Override
    public Map<String, Object> getProjectStatistics(Long projectId, Long departmentId) {
        Map<String, Object> result = new HashMap<>();

        // 构建查询条件
        QueryWrapper<TProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);

        if (projectId != null) {
            queryWrapper.eq("id", projectId);
        }
        if (departmentId != null) {
            queryWrapper.eq("department_id", departmentId);
        }

        List<TProject> projects = projectMapper.selectList(queryWrapper);

        // 项目总数
        result.put("totalProjects", projects.size());

        // 按状态统计
        Map<String, Long> statusStatistics = projects.stream()
                .collect(Collectors.groupingBy(TProject::getStatus, Collectors.counting()));
        result.put("statusStatistics", statusStatistics);

        // 项目进度统计
        List<Map<String, Object>> projectProgress = new ArrayList<>();
        for (TProject project : projects) {
            Map<String, Object> progress = calculateProjectProgress(project.getId());
            progress.put("projectId", project.getId());
            progress.put("projectName", project.getName());
            progress.put("status", project.getStatus());
            projectProgress.add(progress);
        }
        result.put("projectProgress", projectProgress);

        return result;
    }

    @Override
    public Map<String, Object> getPersonalOverview(Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 如果没有指定用户ID，这里应该从上下文获取当前登录用户，暂时先用固定值
        if (userId == null) {
            userId = 1L; // 默认用户ID，实际应该从SecurityContext获取
        }

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate monthStart = today.withDayOfMonth(1);

        // 今日工时
        BigDecimal todayHours = getTotalHours(userId, null, null, today, today);
        result.put("todayHours", todayHours);

        // 本周工时
        BigDecimal weekHours = getTotalHours(userId, null, null, weekStart, today);
        result.put("weekHours", weekHours);

        // 本月工时
        BigDecimal monthHours = getTotalHours(userId, null, null, monthStart, today);
        result.put("monthHours", monthHours);

        // 我的任务统计
        Map<String, Object> myTasks = getTaskStatistics(null, null, userId, null, null);
        result.put("myTasks", myTasks);

        return result;
    }

    @Override
    public Map<String, Object> getTeamOverview(Long groupId, Long departmentId) {
        Map<String, Object> result = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        // 团队本月工时
        BigDecimal teamMonthHours = getTotalHours(null, groupId, null, monthStart, today);
        result.put("teamMonthHours", teamMonthHours);

        // 团队任务统计
        Map<String, Object> teamTasks = getTaskStatistics(null, null, null, groupId, null);
        result.put("teamTasks", teamTasks);

        return result;
    }

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> result = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        // 总体统计
        result.put("totalUsers", getUserCount());
        result.put("totalProjects", getProjectCount());
        result.put("totalTasks", getTaskCount());
        result.put("monthTotalHours", getTotalHours(null, null, null, monthStart, today));

        // 本月任务统计
        Map<String, Object> monthTasks = getTaskStatistics(monthStart, today, null, null, null);
        result.put("monthTasks", monthTasks);

        // 项目状态分布
        Map<String, Object> projectStats = getProjectStatistics(null, null);
        result.put("projectStats", projectStats);

        // 最近7天工时趋势
        Map<String, Object> weekWorkHours = getWorkHoursStatistics(today.minusDays(6), today, null, null, null, "day");
        result.put("weekWorkHours", weekWorkHours);

        return result;
    }

    // 辅助方法
    private Set<Long> getTaskIdsByProject(Long projectId) {
        QueryWrapper<TTask> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId).eq("is_deleted", 0);
        return taskMapper.selectList(wrapper).stream()
                .map(TTask::getId)
                .collect(Collectors.toSet());
    }

    private Map<String, BigDecimal> calculateTimeStatistics(List<TWorkLog> workLogs, String dimension) {
        DateTimeFormatter formatter;
        switch (dimension.toLowerCase()) {
            case "week":
                formatter = DateTimeFormatter.ofPattern("yyyy-'W'ww");
                break;
            case "month":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                break;
            default:
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                break;
        }

        return workLogs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getLogDate().format(formatter),
                        Collectors.reducing(BigDecimal.ZERO, TWorkLog::getHours, BigDecimal::add)
                ));
    }

    private Map<Long, BigDecimal> calculateProjectStatistics(List<TWorkLog> workLogs) {
        // 获取工时日志对应的任务项目映射
        Map<Long, Long> taskProjectMap = new HashMap<>();
        Set<Long> taskIds = workLogs.stream().map(TWorkLog::getTaskId).collect(Collectors.toSet());
        
        if (!taskIds.isEmpty()) {
            QueryWrapper<TTask> wrapper = new QueryWrapper<>();
            wrapper.in("id", taskIds).select("id", "project_id");
            List<TTask> tasks = taskMapper.selectList(wrapper);
            taskProjectMap = tasks.stream()
                    .collect(Collectors.toMap(TTask::getId, task -> task.getProjectId() != null ? task.getProjectId() : 0L));
        }

        final Map<Long, Long> finalTaskProjectMap = taskProjectMap;
        return workLogs.stream()
                .collect(Collectors.groupingBy(
                        log -> finalTaskProjectMap.getOrDefault(log.getTaskId(), 0L),
                        Collectors.reducing(BigDecimal.ZERO, TWorkLog::getHours, BigDecimal::add)
                ));
    }

    private Map<String, Object> calculateProjectProgress(Long projectId) {
        // 获取项目下的所有任务
        QueryWrapper<TTask> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId).eq("is_deleted", 0);
        List<TTask> tasks = taskMapper.selectList(wrapper);

        Map<String, Object> progress = new HashMap<>();
        progress.put("totalTasks", tasks.size());

        if (tasks.isEmpty()) {
            progress.put("completionRate", 0);
            progress.put("averageProgress", 0);
            return progress;
        }

        // 完成的任务数
        long completedTasks = tasks.stream()
                .mapToLong(task -> task.getStatus() == TTask.Status.已完成 ? 1 : 0)
                .sum();
        
        // 平均进度
        double averageProgress = tasks.stream()
                .mapToInt(task -> task.getProgress() != null ? task.getProgress() : 0)
                .average()
                .orElse(0);

        progress.put("completedTasks", completedTasks);
        progress.put("completionRate", (double) completedTasks / tasks.size() * 100);
        progress.put("averageProgress", averageProgress);

        return progress;
    }

    private BigDecimal getTotalHours(Long userId, Long groupId, Long projectId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<TWorkLog> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0)
               .eq("review_status", "已通过")
               .between("log_date", startDate, endDate);

        if (userId != null) {
            wrapper.eq("user_id", userId);
        }
        if (groupId != null) {
            wrapper.eq("group_id", groupId);
        }

        List<TWorkLog> workLogs = workLogMapper.selectList(wrapper);

        if (projectId != null) {
            Set<Long> projectTaskIds = getTaskIdsByProject(projectId);
            workLogs = workLogs.stream()
                    .filter(log -> projectTaskIds.contains(log.getTaskId()))
                    .collect(Collectors.toList());
        }

        return workLogs.stream()
                .map(TWorkLog::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private long getUserCount() {
        // 获取启用状态的用户总数
        QueryWrapper<TUser> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        return userMapper.selectCount(wrapper);
    }

    private long getProjectCount() {
        QueryWrapper<TProject> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        return projectMapper.selectCount(wrapper);
    }

    private long getTaskCount() {
        QueryWrapper<TTask> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        return taskMapper.selectCount(wrapper);
    }
}