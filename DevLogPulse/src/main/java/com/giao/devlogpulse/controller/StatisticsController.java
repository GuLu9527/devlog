package com.giao.devlogpulse.controller;

import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.service.IStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Tag(name = "统计分析", description = "数据统计分析相关接口")
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final IStatisticsService statisticsService;

    @Operation(summary = "获取工时统计数据")
    @GetMapping("/workhours")
    public Result<Map<String, Object>> getWorkHoursStatistics(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "组ID") @RequestParam(required = false) Long groupId,
            @Parameter(description = "项目ID") @RequestParam(required = false) Long projectId,
            @Parameter(description = "统计维度: day/week/month") @RequestParam(defaultValue = "day") String dimension
    ) {
        Map<String, Object> statistics = statisticsService.getWorkHoursStatistics(startDate, endDate, userId, groupId, projectId, dimension);
        return Result.success(statistics);
    }

    @Operation(summary = "获取任务统计数据")
    @GetMapping("/tasks")
    public Result<Map<String, Object>> getTaskStatistics(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "组ID") @RequestParam(required = false) Long groupId,
            @Parameter(description = "项目ID") @RequestParam(required = false) Long projectId
    ) {
        Map<String, Object> statistics = statisticsService.getTaskStatistics(startDate, endDate, userId, groupId, projectId);
        return Result.success(statistics);
    }

    @Operation(summary = "获取项目进度统计数据")
    @GetMapping("/projects")
    public Result<Map<String, Object>> getProjectStatistics(
            @Parameter(description = "项目ID") @RequestParam(required = false) Long projectId,
            @Parameter(description = "部门ID") @RequestParam(required = false) Long departmentId
    ) {
        Map<String, Object> statistics = statisticsService.getProjectStatistics(projectId, departmentId);
        return Result.success(statistics);
    }

    @Operation(summary = "获取个人工作概览")
    @GetMapping("/personal-overview")
    public Result<Map<String, Object>> getPersonalOverview(
            @Parameter(description = "用户ID，不传则使用当前登录用户") @RequestParam(required = false) Long userId
    ) {
        Map<String, Object> overview = statisticsService.getPersonalOverview(userId);
        return Result.success(overview);
    }

    @Operation(summary = "获取团队工作概览")
    @GetMapping("/team-overview")
    public Result<Map<String, Object>> getTeamOverview(
            @Parameter(description = "组ID") @RequestParam(required = false) Long groupId,
            @Parameter(description = "部门ID") @RequestParam(required = false) Long departmentId
    ) {
        Map<String, Object> overview = statisticsService.getTeamOverview(groupId, departmentId);
        return Result.success(overview);
    }

    @Operation(summary = "获取首页仪表板数据")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardData() {
        Map<String, Object> dashboard = statisticsService.getDashboardData();
        return Result.success(dashboard);
    }
}