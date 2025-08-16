package com.giao.devlogpulse.service;

import com.giao.devlogpulse.entity.po.TTask;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * <p>
 * 任务表 服务类
 * </p>
 *
 * @author giao
 * @since 2025-05-22
 */
public interface ITTaskService extends IService<TTask> {
    
    /**
     * 基于工作日志自动更新任务进度
     * 当工作日志审核通过时调用此方法
     *
     * @param taskId 任务ID
     * @param workLogHours 新增工作时长
     * @return 是否更新成功
     */
    boolean updateTaskProgressByWorkLog(Long taskId, BigDecimal workLogHours);
    
    /**
     * 重新计算任务进度
     * 基于所有已通过审核的工作日志累计工时
     *
     * @param taskId 任务ID
     * @return 是否更新成功
     */
    boolean recalculateTaskProgress(Long taskId);
}
