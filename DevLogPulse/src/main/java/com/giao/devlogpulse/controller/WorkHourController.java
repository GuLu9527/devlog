package com.giao.devlogpulse.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.entity.po.TWorkHour;
import com.giao.devlogpulse.model.vo.WorkHourVO;
import com.giao.devlogpulse.entity.po.TTask;
import com.giao.devlogpulse.entity.po.TProject;
import com.giao.devlogpulse.entity.po.TUser;
import com.giao.devlogpulse.service.ITTaskService;
import com.giao.devlogpulse.service.ITProjectService;
import com.giao.devlogpulse.service.ITUserService;
import org.springframework.beans.BeanUtils;
import com.giao.devlogpulse.service.ITWorkHourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工时记录管理控制器
 */
@RestController
@RequestMapping("/work-hours")
@Tag(name = "工时记录管理", description = "工时记录相关接口")
public class WorkHourController {

    @Autowired
    private ITWorkHourService workHourService;
    
    @Autowired
    private ITTaskService taskService;
    
    @Autowired
    private ITProjectService projectService;
    
    @Autowired
    private ITUserService userService;

    /**
     * 创建工时记录
     */
    @PostMapping
    @SaCheckPermission("work:hour:create")
    @Operation(summary = "创建工时记录")
    public Result<Void> createWorkHour(@RequestBody TWorkHour workHour) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        workHour.setUserId(currentUserId);
        
        boolean success = workHourService.createWorkHour(workHour);
        return success ? Result.success() : Result.failed("创建工时记录失败");
    }

    /**
     * 更新工时记录
     */
    @PutMapping("/{id}")
    @SaCheckPermission("work:hour:update")
    @Operation(summary = "更新工时记录")
    public Result<Void> updateWorkHour(@PathVariable Long id, @RequestBody TWorkHour workHour) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        
        TWorkHour existingWorkHour = workHourService.getById(id);
        if (existingWorkHour == null || existingWorkHour.getIsDeleted() == 1) {
            return Result.failed("工时记录不存在");
        }
        
        // 只能修改自己的记录，且只能修改待审核状态的记录
        if (!existingWorkHour.getUserId().equals(currentUserId)) {
            return Result.failed("无权限修改此工时记录");
        }
        if (!TWorkHour.Status.PENDING.equals(existingWorkHour.getStatus())) {
            return Result.failed("只能修改待审核状态的工时记录");
        }
        
        workHour.setId(id);
        workHour.setUserId(currentUserId);
        workHour.setStatus(TWorkHour.Status.PENDING);
        
        boolean success = workHourService.updateById(workHour);
        return success ? Result.success() : Result.failed("更新工时记录失败");
    }

    /**
     * 删除工时记录
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("work:hour:delete")
    @Operation(summary = "删除工时记录")
    public Result<Void> deleteWorkHour(@PathVariable Long id) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        
        TWorkHour workHour = workHourService.getById(id);
        if (workHour == null || workHour.getIsDeleted() == 1) {
            return Result.failed("工时记录不存在");
        }
        
        // 只能删除自己的记录，且只能删除待审核状态的记录
        if (!workHour.getUserId().equals(currentUserId)) {
            return Result.failed("无权限删除此工时记录");
        }
        if (!TWorkHour.Status.PENDING.equals(workHour.getStatus())) {
            return Result.failed("只能删除待审核状态的工时记录");
        }
        
        workHour.setIsDeleted(1);
        boolean success = workHourService.updateById(workHour);
        return success ? Result.success() : Result.failed("删除工时记录失败");
    }

    /**
     * 获取用户工时记录
     */
    @GetMapping("/user")
    @SaCheckPermission("work:hour:view")
    @Operation(summary = "获取用户工时记录")
    public Result<Page<WorkHourVO>> getUserWorkHours(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Long queryUserId = userId != null ? userId : StpUtil.getLoginIdAsLong();
        Page<TWorkHour> workHours = workHourService.getUserWorkHours(queryUserId, startDate, endDate, status, pageNum, pageSize);
        
        // 转换为VO并填充关联信息
        Page<WorkHourVO> voPage = new Page<>(workHours.getCurrent(), workHours.getSize(), workHours.getTotal());
        List<WorkHourVO> voList = workHours.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return Result.success(voPage);
    }

    /**
     * 获取项目工时统计
     */
    @GetMapping("/project/{projectId}/stats")
    @SaCheckPermission("work:hour:view")
    @Operation(summary = "获取项目工时统计")
    public Result<Map<String, Object>> getProjectWorkHourStats(
            @PathVariable Long projectId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        Map<String, Object> stats = workHourService.getProjectWorkHourStats(projectId, startDate, endDate);
        return Result.success(stats);
    }

    /**
     * 获取任务工时统计
     */
    @GetMapping("/task/{taskId}/stats")
    @SaCheckPermission("work:hour:view")
    @Operation(summary = "获取任务工时统计")
    public Result<Map<String, Object>> getTaskWorkHourStats(@PathVariable Long taskId) {
        Map<String, Object> stats = workHourService.getTaskWorkHourStats(taskId);
        return Result.success(stats);
    }

    /**
     * 审核工时记录
     */
    @PostMapping("/{id}/review")
    @SaCheckPermission("work:hour:review")
    @Operation(summary = "审核工时记录")
    public Result<Void> reviewWorkHour(@PathVariable Long id, @RequestBody ReviewWorkHourRequest request) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        
        boolean success = workHourService.reviewWorkHour(
            id, 
            request.getStatus(), 
            request.getReviewNote(), 
            currentUserId
        );
        return success ? Result.success() : Result.failed("审核工时记录失败");
    }

    /**
     * 获取待审核工时记录
     */
    @GetMapping("/pending")
    @SaCheckPermission("work:hour:review")
    @Operation(summary = "获取待审核工时记录")
    public Result<Page<TWorkHour>> getPendingWorkHours(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Page<TWorkHour> workHours = workHourService.getPendingWorkHours(pageNum, pageSize);
        return Result.success(workHours);
    }

    /**
     * 批量审核工时记录
     */
    @PostMapping("/batch-review")
    @SaCheckPermission("work:hour:review")
    @Operation(summary = "批量审核工时记录")
    public Result<Void> batchReviewWorkHours(@RequestBody BatchReviewWorkHourRequest request) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        
        boolean success = workHourService.batchReviewWorkHours(
            request.getWorkHourIds(),
            request.getStatus(),
            request.getReviewNote(),
            currentUserId
        );
        return success ? Result.success() : Result.failed("批量审核工时记录失败");
    }

    /**
     * 获取用户工时统计
     */
    @GetMapping("/user/{userId}/stats")
    @SaCheckPermission("work:hour:view")
    @Operation(summary = "获取用户工时统计")
    public Result<Map<String, Object>> getUserWorkHourStats(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        Map<String, Object> stats = workHourService.getUserWorkHourStats(userId, startDate, endDate);
        return Result.success(stats);
    }

    /**
     * 获取团队工时排行
     */
    @GetMapping("/team/ranking")
    @SaCheckPermission("work:hour:view")
    @Operation(summary = "获取团队工时排行")
    public Result<List<Map<String, Object>>> getTeamWorkHourRanking(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Map<String, Object>> ranking = workHourService.getTeamWorkHourRanking(startDate, endDate, limit);
        return Result.success(ranking);
    }

    /**
     * 获取用户日工时汇总
     */
    @GetMapping("/user/{userId}/daily")
    @SaCheckPermission("work:hour:view")
    @Operation(summary = "获取用户日工时汇总")
    public Result<BigDecimal> getUserDailyWorkHours(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        
        BigDecimal dailyHours = workHourService.getUserDailyWorkHours(userId, date);
        return Result.success(dailyHours);
    }

    /**
     * 审核工时记录请求体
     */
    public static class ReviewWorkHourRequest {
        private TWorkHour.Status status;
        private String reviewNote;

        public TWorkHour.Status getStatus() { return status; }
        public void setStatus(TWorkHour.Status status) { this.status = status; }

        public String getReviewNote() { return reviewNote; }
        public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }
    }

    /**
     * 批量审核工时记录请求体
     */
    public static class BatchReviewWorkHourRequest {
        private List<Long> workHourIds;
        private TWorkHour.Status status;
        private String reviewNote;

        public List<Long> getWorkHourIds() { return workHourIds; }
        public void setWorkHourIds(List<Long> workHourIds) { this.workHourIds = workHourIds; }

        public TWorkHour.Status getStatus() { return status; }
        public void setStatus(TWorkHour.Status status) { this.status = status; }

        public String getReviewNote() { return reviewNote; }
        public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }
    }
    
    /**
     * 转换为WorkHourVO
     */
    private WorkHourVO convertToVO(TWorkHour workHour) {
        WorkHourVO vo = new WorkHourVO();
        BeanUtils.copyProperties(workHour, vo);
        
        // 设置类型和状态文本
        if (workHour.getType() != null) {
            vo.setTypeText(workHour.getType().name());
        }
        if (workHour.getStatus() != null) {
            vo.setStatusText(workHour.getStatus().name());
        }
        
        // 获取任务信息
        if (workHour.getTaskId() != null) {
            try {
                TTask task = taskService.getById(workHour.getTaskId());
                if (task != null && task.getIsDeleted() != 1) {
                    vo.setTaskTitle(task.getTitle());
                }
            } catch (Exception e) {
                // 忽略任务查询异常
            }
        }
        
        // 获取项目信息
        if (workHour.getProjectId() != null) {
            try {
                TProject project = projectService.getById(workHour.getProjectId());
                if (project != null && project.getIsDeleted() != 1) {
                    vo.setProjectName(project.getName());
                }
            } catch (Exception e) {
                // 忽略项目查询异常
            }
        }
        
        // 获取用户信息
        if (workHour.getUserId() != null) {
            try {
                TUser user = userService.getById(workHour.getUserId());
                if (user != null && user.getStatus() == 1) {
                    vo.setUserName(user.getRealName());
                }
            } catch (Exception e) {
                // 忽略用户查询异常
            }
        }
        
        // 获取审核人信息
        if (workHour.getReviewerId() != null) {
            try {
                TUser reviewer = userService.getById(workHour.getReviewerId());
                if (reviewer != null && reviewer.getStatus() == 1) {
                    vo.setReviewerName(reviewer.getRealName());
                }
            } catch (Exception e) {
                // 忽略审核人查询异常
            }
        }
        
        return vo;
    }
}