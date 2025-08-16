package com.giao.devlogpulse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.common.exception.BusinessException;
import com.giao.devlogpulse.entity.po.TTask;
import com.giao.devlogpulse.entity.po.TUser;
import com.giao.devlogpulse.entity.po.TWorkLog;
import com.giao.devlogpulse.model.dto.WorkLogCreateDTO;
import com.giao.devlogpulse.model.dto.WorkLogPageDTO;
import com.giao.devlogpulse.model.dto.WorkLogReviewDTO;
import com.giao.devlogpulse.model.dto.WorkLogUpdateDTO;
import com.giao.devlogpulse.model.vo.WorkLogVO;
import com.giao.devlogpulse.service.INotificationService;
import com.giao.devlogpulse.service.ITTaskService;
import com.giao.devlogpulse.service.ITUserService;
import com.giao.devlogpulse.service.ITWorkLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/worklogs")
public class WorkLogController {

    @Autowired
    private ITWorkLogService workLogService;
    
    @Autowired
    private ITUserService userService;
    
    @Autowired
    private ITTaskService taskService;
    
    @Autowired
    private INotificationService notificationService;

    /**
     * 创建工作日志
     */
    @PostMapping
    public Result<WorkLogVO> create(@Valid @RequestBody WorkLogCreateDTO createDTO, HttpServletRequest request) {
        // 获取当前用户ID（从JWT token中获取）
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证任务存在性
        TTask task = taskService.getById(createDTO.getTaskId());
        if (task == null || task.getIsDeleted() == 1) {
            throw new BusinessException("关联任务不存在");
        }
        
        // 创建工作日志实体
        TWorkLog workLog = new TWorkLog();
        BeanUtils.copyProperties(createDTO, workLog);
        workLog.setUserId(userId);
        workLog.setReviewStatus(TWorkLog.ReviewStatus.待审核);
        workLog.setIsDeleted(0);
        workLog.setCreateTime(LocalDateTime.now());
        workLog.setUpdateTime(LocalDateTime.now());
        
        if (!workLogService.save(workLog)) {
            throw new BusinessException("工作日志创建失败");
        }
        
        // 构建返回VO
        WorkLogVO workLogVO = buildWorkLogVO(workLog);
        return Result.success(workLogVO);
    }

    /**
     * 分页查询工作日志
     */
    @GetMapping
    public Result<IPage<WorkLogVO>> list(@Valid WorkLogPageDTO pageDTO) {
        Page<TWorkLog> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        LambdaQueryWrapper<TWorkLog> queryWrapper = buildQueryWrapper(pageDTO);
        
        IPage<TWorkLog> workLogPage = workLogService.page(page, queryWrapper);
        
        // 转换为VO
        List<WorkLogVO> workLogVOList = workLogPage.getRecords().stream()
                .map(this::buildWorkLogVO)
                .collect(Collectors.toList());
        
        Page<WorkLogVO> resultPage = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize(), workLogPage.getTotal());
        resultPage.setRecords(workLogVOList);
        
        return Result.success(resultPage);
    }

    /**
     * 根据ID查询工作日志详情
     */
    @GetMapping("/{id}")
    public Result<WorkLogVO> getById(@PathVariable Long id) {
        TWorkLog workLog = workLogService.getById(id);
        if (workLog == null || workLog.getIsDeleted() == 1) {
            throw new BusinessException("工作日志不存在");
        }
        
        WorkLogVO workLogVO = buildWorkLogVO(workLog);
        return Result.success(workLogVO);
    }

    /**
     * 更新工作日志
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, 
                              @Valid @RequestBody WorkLogUpdateDTO updateDTO,
                              HttpServletRequest request) {
        // 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 检查工作日志是否存在
        TWorkLog existingLog = workLogService.getById(id);
        if (existingLog == null || existingLog.getIsDeleted() == 1) {
            throw new BusinessException("工作日志不存在");
        }
        
        // 检查权限（只能修改自己的日志）
        if (!existingLog.getUserId().equals(userId)) {
            throw new BusinessException("无权修改他人的工作日志");
        }
        
        // 检查是否可以编辑
        if (!workLogService.canEdit(id)) {
            throw new BusinessException("当前状态的工作日志不可编辑");
        }
        
        // 验证任务存在性（如果更新了任务ID）
        if (updateDTO.getTaskId() != null && !updateDTO.getTaskId().equals(existingLog.getTaskId())) {
            TTask task = taskService.getById(updateDTO.getTaskId());
            if (task == null || task.getIsDeleted() == 1) {
                throw new BusinessException("关联任务不存在");
            }
        }
        
        // 更新工作日志
        TWorkLog workLog = new TWorkLog();
        BeanUtils.copyProperties(updateDTO, workLog);
        workLog.setId(id);
        workLog.setReviewStatus(TWorkLog.ReviewStatus.待审核); // 修改后重新审核
        workLog.setReviewerId(null); // 清空审核信息
        workLog.setReviewComment(null);
        workLog.setReviewTime(null);
        workLog.setUpdateTime(LocalDateTime.now());
        
        if (!workLogService.updateById(workLog)) {
            throw new BusinessException("工作日志更新失败");
        }
        
        return Result.success();
    }

    /**
     * 审核工作日志
     */
    @PutMapping("/{id}/review")
    @SaCheckPermission("worklog:review")
    public Result<Void> review(@PathVariable Long id,
                              @Valid @RequestBody WorkLogReviewDTO reviewDTO,
                              HttpServletRequest request) {
        // 获取当前用户ID（审核人）
        Long reviewerId = StpUtil.getLoginIdAsLong();
        
        // 检查工作日志是否存在
        TWorkLog existingLog = workLogService.getById(id);
        if (existingLog == null || existingLog.getIsDeleted() == 1) {
            throw new BusinessException("工作日志不存在");
        }
        
        // 检查是否可以审核（不能审核自己的日志）
        if (existingLog.getUserId().equals(reviewerId)) {
            throw new BusinessException("不能审核自己的工作日志");
        }
        
        // 检查审核状态
        if (existingLog.getReviewStatus() != TWorkLog.ReviewStatus.待审核) {
            throw new BusinessException("该工作日志已审核，无法重复审核");
        }
        
        // 更新审核信息
        TWorkLog workLog = new TWorkLog();
        workLog.setId(id);
        workLog.setReviewStatus(reviewDTO.getReviewStatus());
        workLog.setReviewComment(reviewDTO.getReviewComment());
        workLog.setReviewerId(reviewerId);
        workLog.setReviewTime(LocalDateTime.now());
        workLog.setUpdateTime(LocalDateTime.now());
        
        if (!workLogService.updateById(workLog)) {
            throw new BusinessException("工作日志审核失败");
        }
        
        // 如果审核通过，自动更新任务进度
        if (reviewDTO.getReviewStatus() == TWorkLog.ReviewStatus.已通过) {
            try {
                taskService.updateTaskProgressByWorkLog(existingLog.getTaskId(), existingLog.getHours());
            } catch (Exception e) {
                // 任务更新失败不影响审核结果，只记录日志
                // 日志已在TaskService中记录，这里不再重复记录
            }
        }
        
        // 发送审核结果通知给提交人
        try {
            if (notificationService instanceof com.giao.devlogpulse.service.impl.NotificationServiceImpl) {
                com.giao.devlogpulse.service.impl.NotificationServiceImpl notificationServiceImpl = 
                    (com.giao.devlogpulse.service.impl.NotificationServiceImpl) notificationService;
                notificationServiceImpl.sendWorklogReviewResultNotification(
                    existingLog.getId(), reviewDTO.getReviewStatus(), reviewDTO.getReviewComment(), reviewerId);
            }
        } catch (Exception e) {
            // 通知发送失败不影响审核结果
        }
        
        return Result.success();
    }

    /**
     * 删除工作日志（软删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        // 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 检查工作日志是否存在
        TWorkLog workLog = workLogService.getById(id);
        if (workLog == null || workLog.getIsDeleted() == 1) {
            throw new BusinessException("工作日志不存在");
        }
        
        // 检查权限（只能删除自己的日志）
        if (!workLog.getUserId().equals(userId)) {
            throw new BusinessException("无权删除他人的工作日志");
        }
        
        // 检查是否可以删除（已通过审核的日志可能不允许删除）
        if (workLog.getReviewStatus() == TWorkLog.ReviewStatus.已通过) {
            throw new BusinessException("已通过审核的工作日志不允许删除");
        }
        
        // 软删除
        TWorkLog updateLog = new TWorkLog();
        updateLog.setId(id);
        updateLog.setIsDeleted(1);
        updateLog.setUpdateTime(LocalDateTime.now());
        
        if (!workLogService.updateById(updateLog)) {
            throw new BusinessException("工作日志删除失败");
        }
        
        return Result.success();
    }

    /**
     * 获取工作日志统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(@RequestParam(required = false) Long taskId,
                                                     @RequestParam(required = false) Long userId,
                                                     @RequestParam(required = false) String startDate,
                                                     @RequestParam(required = false) String endDate) {
        // 这里需要根据实际需求实现统计逻辑
        Map<String, Object> statistics = workLogService.getWorkLogStatistics(
            taskId, userId, 
            startDate != null ? java.time.LocalDate.parse(startDate) : null,
            endDate != null ? java.time.LocalDate.parse(endDate) : null
        );
        
        return Result.success(statistics);
    }

    /**
     * 批量审核工作日志
     */
    @PutMapping("/batch/review")
    @SaCheckPermission("worklog:review")
    public Result<Void> batchReview(@RequestParam List<Long> ids,
                                   @Valid @RequestBody WorkLogReviewDTO reviewDTO,
                                   HttpServletRequest request) {
        // 获取当前用户ID（审核人）
        Long reviewerId = StpUtil.getLoginIdAsLong();
        
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要审核的工作日志");
        }
        
        // 批量审核
        for (Long id : ids) {
            TWorkLog existingLog = workLogService.getById(id);
            if (existingLog == null || existingLog.getIsDeleted() == 1) {
                continue; // 跳过不存在的日志
            }
            
            // 检查是否可以审核
            if (existingLog.getUserId().equals(reviewerId) || 
                existingLog.getReviewStatus() != TWorkLog.ReviewStatus.待审核) {
                continue; // 跳过无法审核的日志
            }
            
            // 更新审核信息
            TWorkLog workLog = new TWorkLog();
            workLog.setId(id);
            workLog.setReviewStatus(reviewDTO.getReviewStatus());
            workLog.setReviewComment(reviewDTO.getReviewComment());
            workLog.setReviewerId(reviewerId);
            workLog.setReviewTime(LocalDateTime.now());
            workLog.setUpdateTime(LocalDateTime.now());
            
            if (workLogService.updateById(workLog)) {
                // 如果审核通过，自动更新任务进度
                if (reviewDTO.getReviewStatus() == TWorkLog.ReviewStatus.已通过) {
                    try {
                        taskService.updateTaskProgressByWorkLog(existingLog.getTaskId(), existingLog.getHours());
                    } catch (Exception e) {
                        // 任务更新失败不影响审核结果，只记录日志
                        // 日志已在TaskService中记录，这里不再重复记录
                    }
                }
            }
        }
        
        return Result.success();
    }

    /**
     * 批量删除工作日志
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestParam List<Long> ids, HttpServletRequest request) {
        // 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的工作日志");
        }
        
        int successCount = 0;
        for (Long id : ids) {
            TWorkLog workLog = workLogService.getById(id);
            if (workLog == null || workLog.getIsDeleted() == 1) {
                continue; // 跳过不存在的日志
            }
            
            // 检查权限（只能删除自己的日志）
            if (!workLog.getUserId().equals(userId)) {
                continue; // 跳过无权删除的日志
            }
            
            // 检查是否可以删除
            if (workLog.getReviewStatus() == TWorkLog.ReviewStatus.已通过) {
                continue; // 跳过已通过审核的日志
            }
            
            // 软删除
            TWorkLog updateLog = new TWorkLog();
            updateLog.setId(id);
            updateLog.setIsDeleted(1);
            updateLog.setUpdateTime(LocalDateTime.now());
            
            if (workLogService.updateById(updateLog)) {
                successCount++;
            }
        }
        
        return Result.<Void>success().setMessage("批量删除完成，成功删除 " + successCount + " 条记录");
    }

    /**
     * 批量重新提交审核
     */
    @PutMapping("/batch/resubmit")
    public Result<Void> batchResubmit(@RequestParam List<Long> ids, HttpServletRequest request) {
        // 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要重新提交的工作日志");
        }
        
        int successCount = 0;
        for (Long id : ids) {
            TWorkLog workLog = workLogService.getById(id);
            if (workLog == null || workLog.getIsDeleted() == 1) {
                continue; // 跳过不存在的日志
            }
            
            // 检查权限（只能操作自己的日志）
            if (!workLog.getUserId().equals(userId)) {
                continue; // 跳过无权操作的日志
            }
            
            // 检查是否可以重新提交（只有被拒绝的日志可以重新提交）
            if (workLog.getReviewStatus() != TWorkLog.ReviewStatus.已拒绝) {
                continue; // 跳过不是被拒绝状态的日志
            }
            
            // 重新提交审核
            TWorkLog updateLog = new TWorkLog();
            updateLog.setId(id);
            updateLog.setReviewStatus(TWorkLog.ReviewStatus.待审核);
            updateLog.setReviewerId(null);
            updateLog.setReviewComment(null);
            updateLog.setReviewTime(null);
            updateLog.setUpdateTime(LocalDateTime.now());
            
            if (workLogService.updateById(updateLog)) {
                successCount++;
            }
        }
        
        return Result.<Void>success().setMessage("批量重新提交完成，成功提交 " + successCount + " 条记录");
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<TWorkLog> buildQueryWrapper(WorkLogPageDTO pageDTO) {
        LambdaQueryWrapper<TWorkLog> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(TWorkLog::getIsDeleted, 0);
        
        if (pageDTO.getTaskId() != null) {
            queryWrapper.eq(TWorkLog::getTaskId, pageDTO.getTaskId());
        }
        if (pageDTO.getUserId() != null) {
            queryWrapper.eq(TWorkLog::getUserId, pageDTO.getUserId());
        }
        if (pageDTO.getGroupId() != null) {
            queryWrapper.eq(TWorkLog::getGroupId, pageDTO.getGroupId());
        }
        if (pageDTO.getReviewerId() != null) {
            queryWrapper.eq(TWorkLog::getReviewerId, pageDTO.getReviewerId());
        }
        if (pageDTO.getReviewStatus() != null) {
            queryWrapper.eq(TWorkLog::getReviewStatus, pageDTO.getReviewStatus());
        }
        if (pageDTO.getStartDate() != null) {
            queryWrapper.ge(TWorkLog::getLogDate, pageDTO.getStartDate());
        }
        if (pageDTO.getEndDate() != null) {
            queryWrapper.le(TWorkLog::getLogDate, pageDTO.getEndDate());
        }
        if (StringUtils.hasText(pageDTO.getContent())) {
            queryWrapper.like(TWorkLog::getContent, pageDTO.getContent());
        }
        
        // 默认按创建时间倒序
        queryWrapper.orderByDesc(TWorkLog::getCreateTime);
        
        return queryWrapper;
    }

    /**
     * 构建WorkLogVO
     */
    private WorkLogVO buildWorkLogVO(TWorkLog workLog) {
        WorkLogVO vo = new WorkLogVO();
        BeanUtils.copyProperties(workLog, vo);
        
        // 设置审核状态描述
        vo.setReviewStatusText(workLog.getReviewStatus().name());
        
        // 获取用户信息
        if (workLog.getUserId() != null) {
            TUser user = userService.getById(workLog.getUserId());
            if (user != null) {
                vo.setUserName(user.getRealName());
            }
        }
        
        // 获取审核人信息
        if (workLog.getReviewerId() != null) {
            TUser reviewer = userService.getById(workLog.getReviewerId());
            if (reviewer != null) {
                vo.setReviewerName(reviewer.getRealName());
            }
        }
        
        // 获取任务信息
        if (workLog.getTaskId() != null) {
            TTask task = taskService.getById(workLog.getTaskId());
            if (task != null) {
                vo.setTaskTitle(task.getTitle());
            }
        }
        
        // 设置操作权限
        vo.setEditable(workLogService.canEdit(workLog.getId()));
        vo.setDeletable(workLog.getReviewStatus() != TWorkLog.ReviewStatus.已通过);
        
        return vo;
    }
}