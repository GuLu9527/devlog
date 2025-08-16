package com.giao.devlogpulse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.common.api.ResultCode;
import com.giao.devlogpulse.common.exception.BusinessException;
import com.giao.devlogpulse.entity.po.TTask;
import com.giao.devlogpulse.entity.po.TProject;
import com.giao.devlogpulse.entity.po.TUser;
import com.giao.devlogpulse.service.ITProjectService;
import com.giao.devlogpulse.service.ITUserService;
import com.giao.devlogpulse.model.dto.TaskCreateDTO;
import com.giao.devlogpulse.model.dto.TaskPageDTO;
import com.giao.devlogpulse.model.dto.TaskUpdateDTO;
import com.giao.devlogpulse.model.vo.TaskVO;
import com.giao.devlogpulse.service.ITTaskService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITTaskService taskService;
    
    @Autowired
    private ITProjectService projectService;
    
    @Autowired
    private ITUserService userService;

    /**
     * 创建任务
     */
    @PostMapping
    @SaCheckPermission("task:create")
    public Result<TaskVO> create(@Valid @RequestBody TaskCreateDTO createDTO) {
        // 检查依赖任务是否存在
        if (createDTO.getDependTaskId() != null) {
            TTask dependTask = taskService.getById(createDTO.getDependTaskId());
            if (dependTask == null || dependTask.getIsDeleted() == 1) {
                throw new BusinessException(ResultCode.TASK_NOT_FOUND, "依赖任务不存在");
            }
        }

        TTask task = new TTask();
        BeanUtils.copyProperties(createDTO, task);
        
        // TODO: 从JWT中获取当前用户ID
        task.setCreatorId(1L); // 临时硬编码
        task.setStatus(TTask.Status.待处理);
        task.setProgress(0);
        task.setActualHours(BigDecimal.ZERO);
        task.setIsDeleted(0);
        task.setDeleteStatus(0);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());

        taskService.save(task);
        
        TaskVO taskVO = convertToVO(task);
        return Result.success(taskVO);
    }

    /**
     * 分页查询任务列表
     */
    @GetMapping
    @SaCheckPermission("task:list")
    public Result<IPage<TaskVO>> list(TaskPageDTO pageDTO) {
        Page<TTask> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        LambdaQueryWrapper<TTask> queryWrapper = buildQueryWrapper(pageDTO);
        
        IPage<TTask> taskPage = taskService.page(page, queryWrapper);
        
        // 转换为VO
        IPage<TaskVO> voPage = taskPage.convert(this::convertToVO);
        
        return Result.success(voPage);
    }

    /**
     * 获取所有任务列表（不分页）
     */
    @GetMapping("/all")
    @SaCheckPermission("task:list")
    public Result<List<TaskVO>> listAll(TaskPageDTO pageDTO) {
        LambdaQueryWrapper<TTask> queryWrapper = buildQueryWrapper(pageDTO);
        
        List<TTask> taskList = taskService.list(queryWrapper);
        
        List<TaskVO> voList = taskList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return Result.success(voList);
    }

    /**
     * 获取简化任务列表（用于选择器等）
     */
    @GetMapping("/simple")
    @SaCheckPermission("task:list")
    public Result<List<TaskVO>> getSimpleList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String title) {
        
        Page<TTask> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TTask> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询未删除的任务
        queryWrapper.eq(TTask::getIsDeleted, 0);
        
        // 状态筛选 - 忽略"全部"或空值
        if (StringUtils.hasText(status) && !"全部".equals(status)) {
            try {
                TTask.Status taskStatus = TTask.Status.valueOf(status);
                queryWrapper.eq(TTask::getStatus, taskStatus);
            } catch (IllegalArgumentException e) {
                // 忽略无效的状态值
            }
        }
        
        if (projectId != null) {
            queryWrapper.eq(TTask::getProjectId, projectId);
        }
        
        if (StringUtils.hasText(title)) {
            queryWrapper.like(TTask::getTitle, title);
        }
        
        // 按创建时间倒序
        queryWrapper.orderByDesc(TTask::getCreateTime);
        
        IPage<TTask> taskPage = taskService.page(page, queryWrapper);
        List<TaskVO> voList = taskPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return Result.success(voList);
    }

    /**
     * 根据ID查询任务详情
     */
    @GetMapping("/{id}")
    public Result<TaskVO> getById(@PathVariable Long id) {
        TTask task = taskService.getById(id);
        if (task == null || task.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        
        TaskVO taskVO = convertToVO(task);
        return Result.success(taskVO);
    }

    /**
     * 更新任务信息
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO updateDTO) {
        TTask existingTask = taskService.getById(id);
        if (existingTask == null || existingTask.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        
        // 检查依赖任务是否存在
        if (updateDTO.getDependTaskId() != null && !updateDTO.getDependTaskId().equals(id)) {
            TTask dependTask = taskService.getById(updateDTO.getDependTaskId());
            if (dependTask == null || dependTask.getIsDeleted() == 1) {
                throw new BusinessException(ResultCode.TASK_NOT_FOUND, "依赖任务不存在");
            }
            
            // 防止循环依赖
            if (updateDTO.getDependTaskId().equals(id)) {
                throw new BusinessException(ResultCode.VALIDATE_FAILED, "任务不能依赖自己");
            }
        }
        
        BeanUtils.copyProperties(updateDTO, existingTask);
        existingTask.setUpdateTime(LocalDateTime.now());
        
        taskService.updateById(existingTask);
        return Result.success();
    }

    /**
     * 删除任务（软删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        TTask task = taskService.getById(id);
        if (task == null || task.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        
        // 检查删除权限
        if (task.getDeleteStatus() == 1) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "此任务不允许删除");
        }
        
        task.setIsDeleted(1);
        task.setUpdateTime(LocalDateTime.now());
        
        taskService.updateById(task);
        return Result.success();
    }

    /**
     * 批量删除任务
     */
    @DeleteMapping
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "任务ID列表不能为空");
        }
        
        List<TTask> tasks = taskService.listByIds(ids);
        for (TTask task : tasks) {
            if (task.getDeleteStatus() == 1) {
                throw new BusinessException(ResultCode.VALIDATE_FAILED, 
                        "任务 '" + task.getTitle() + "' 不允许删除");
            }
            task.setIsDeleted(1);
            task.setUpdateTime(LocalDateTime.now());
        }
        
        taskService.updateBatchById(tasks);
        return Result.success();
    }

    /**
     * 更新任务状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam TTask.Status status) {
        TTask task = taskService.getById(id);
        if (task == null || task.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        
        // 保存旧状态用于比较
        TTask.Status oldStatus = task.getStatus();
        
        task.setStatus(status);
        task.setUpdateTime(LocalDateTime.now());
        
        // 如果状态改为已完成，设置进度为100%
        if (status == TTask.Status.已完成) {
            task.setProgress(100);
        }
        
        taskService.updateById(task);
        
        // 任务状态发生变化时，自动更新项目进度
        if (oldStatus != status && task.getProjectId() != null) {
            projectService.updateProjectProgress(task.getProjectId());
        }
        
        return Result.success();
    }

    /**
     * 更新任务进度
     */
    @PutMapping("/{id}/progress")
    public Result<Void> updateProgress(@PathVariable Long id, @RequestParam Integer progress) {
        if (progress < 0 || progress > 100) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "进度必须在0-100之间");
        }
        
        TTask task = taskService.getById(id);
        if (task == null || task.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        
        task.setProgress(progress);
        task.setUpdateTime(LocalDateTime.now());
        
        // 如果进度为100%，自动设置状态为已完成
        if (progress == 100) {
            task.setStatus(TTask.Status.已完成);
        } else if (task.getStatus() == TTask.Status.待处理 && progress > 0) {
            task.setStatus(TTask.Status.进行中);
        }
        
        taskService.updateById(task);
        return Result.success();
    }

    /**
     * 批量更新任务状态
     */
    @PutMapping("/batch/status")
    @SaCheckPermission("task:update")
    public Result<Void> batchUpdateStatus(@RequestParam List<Long> ids, @RequestParam TTask.Status status) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "任务ID列表不能为空");
        }
        
        List<TTask> tasks = taskService.listByIds(ids);
        int successCount = 0;
        
        for (TTask task : tasks) {
            if (task == null || task.getIsDeleted() == 1) {
                continue;
            }
            
            task.setStatus(status);
            task.setUpdateTime(LocalDateTime.now());
            
            // 如果状态改为已完成，设置进度为100%
            if (status == TTask.Status.已完成) {
                task.setProgress(100);
            }
            
            successCount++;
        }
        
        if (successCount > 0) {
            taskService.updateBatchById(tasks);
        }
        
        return Result.<Void>success().setMessage("批量状态更新完成，成功更新 " + successCount + " 个任务");
    }

    /**
     * 批量分配任务负责人
     */
    @PutMapping("/batch/assign")
    @SaCheckPermission("task:assign")
    public Result<Void> batchAssignTasks(@RequestParam List<Long> ids, @RequestParam Long assigneeId) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "任务ID列表不能为空");
        }
        
        List<TTask> tasks = taskService.listByIds(ids);
        int successCount = 0;
        
        for (TTask task : tasks) {
            if (task == null || task.getIsDeleted() == 1) {
                continue;
            }
            
            task.setAssigneeId(assigneeId);
            task.setUpdateTime(LocalDateTime.now());
            
            successCount++;
        }
        
        if (successCount > 0) {
            taskService.updateBatchById(tasks);
        }
        
        return Result.<Void>success().setMessage("批量分配完成，成功分配 " + successCount + " 个任务");
    }

    /**
     * 批量更新任务优先级
     */
    @PutMapping("/batch/priority")
    @SaCheckPermission("task:update")
    public Result<Void> batchUpdatePriority(@RequestParam List<Long> ids, @RequestParam TTask.Priority priority) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "任务ID列表不能为空");
        }
        
        List<TTask> tasks = taskService.listByIds(ids);
        int successCount = 0;
        
        for (TTask task : tasks) {
            if (task == null || task.getIsDeleted() == 1) {
                continue;
            }
            
            task.setPriority(priority);
            task.setUpdateTime(LocalDateTime.now());
            
            successCount++;
        }
        
        if (successCount > 0) {
            taskService.updateBatchById(tasks);
        }
        
        return Result.<Void>success().setMessage("批量优先级更新完成，成功更新 " + successCount + " 个任务");
    }

    /**
     * 批量重新计算任务进度
     */
    @PostMapping("/batch/recalculate-progress")
    @SaCheckPermission("task:update")
    public Result<Void> batchRecalculateProgress(@RequestParam List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "任务ID列表不能为空");
        }
        
        int successCount = 0;
        for (Long taskId : ids) {
            try {
                if (taskService.recalculateTaskProgress(taskId)) {
                    successCount++;
                }
            } catch (Exception e) {
                // 个别任务计算失败不影响整体操作
            }
        }
        
        return Result.<Void>success().setMessage("批量进度重新计算完成，成功处理 " + successCount + " 个任务");
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<TTask> buildQueryWrapper(TaskPageDTO pageDTO) {
        LambdaQueryWrapper<TTask> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询未删除的任务
        queryWrapper.eq(TTask::getIsDeleted, 0);
        
        if (StringUtils.hasText(pageDTO.getTitle())) {
            queryWrapper.like(TTask::getTitle, pageDTO.getTitle());
        }
        if (pageDTO.getStatus() != null) {
            queryWrapper.eq(TTask::getStatus, pageDTO.getStatus());
        }
        if (pageDTO.getPriority() != null) {
            queryWrapper.eq(TTask::getPriority, pageDTO.getPriority());
        }
        if (pageDTO.getProjectId() != null) {
            queryWrapper.eq(TTask::getProjectId, pageDTO.getProjectId());
        }
        if (pageDTO.getCreatorId() != null) {
            queryWrapper.eq(TTask::getCreatorId, pageDTO.getCreatorId());
        }
        if (pageDTO.getAssigneeId() != null) {
            queryWrapper.eq(TTask::getAssigneeId, pageDTO.getAssigneeId());
        }
        if (pageDTO.getDepartmentId() != null) {
            queryWrapper.eq(TTask::getDepartmentId, pageDTO.getDepartmentId());
        }
        if (pageDTO.getGroupId() != null) {
            queryWrapper.eq(TTask::getGroupId, pageDTO.getGroupId());
        }
        
        // 时间范围查询
        if (pageDTO.getStartTimeBegin() != null) {
            queryWrapper.ge(TTask::getStartTime, pageDTO.getStartTimeBegin());
        }
        if (pageDTO.getStartTimeEnd() != null) {
            queryWrapper.le(TTask::getStartTime, pageDTO.getStartTimeEnd());
        }
        if (pageDTO.getDeadlineBegin() != null) {
            queryWrapper.ge(TTask::getDeadline, pageDTO.getDeadlineBegin());
        }
        if (pageDTO.getDeadlineEnd() != null) {
            queryWrapper.le(TTask::getDeadline, pageDTO.getDeadlineEnd());
        }
        
        queryWrapper.orderByDesc(TTask::getCreateTime);
        
        return queryWrapper;
    }

    /**
     * 转换为VO
     */
    private TaskVO convertToVO(TTask task) {
        TaskVO vo = new TaskVO();
        BeanUtils.copyProperties(task, vo);
        
        // 设置状态和优先级文本
        vo.setStatusText(task.getStatus() != null ? task.getStatus().name() : "");
        vo.setPriorityText(task.getPriority() != null ? task.getPriority().name() : "");
        
        // 计算是否逾期和剩余天数
        if (task.getDeadline() != null) {
            LocalDateTime now = LocalDateTime.now();
            long remainingDays = ChronoUnit.DAYS.between(now, task.getDeadline());
            vo.setRemainingDays(remainingDays);
            vo.setOverdue(remainingDays < 0 && task.getStatus() != TTask.Status.已完成);
        }
        
        // 设置关联信息
        // 获取项目名称
        if (task.getProjectId() != null) {
            try {
                TProject project = projectService.getById(task.getProjectId());
                if (project != null && project.getIsDeleted() != 1) {
                    vo.setProjectName(project.getName());
                }
            } catch (Exception e) {
                // 忽略项目查询异常
            }
        }
        
        // 获取创建人姓名
        if (task.getCreatorId() != null) {
            try {
                TUser creator = userService.getById(task.getCreatorId());
                if (creator != null && creator.getStatus() == 1) {
                    vo.setCreatorName(creator.getRealName());
                }
            } catch (Exception e) {
                // 忽略用户查询异常
            }
        }
        
        // 获取负责人姓名
        if (task.getAssigneeId() != null) {
            try {
                TUser assignee = userService.getById(task.getAssigneeId());
                if (assignee != null && assignee.getStatus() == 1) {
                    vo.setAssigneeName(assignee.getRealName());
                }
            } catch (Exception e) {
                // 忽略用户查询异常
            }
        }
        
        return vo;
    }
}