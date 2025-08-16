package com.giao.devlogpulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.giao.devlogpulse.entity.po.TWorkLog;

import java.time.LocalDate;
import java.util.Map;

/**
 * <p>
 * 工作日志表 服务类
 * </p>
 *
 * @author giao
 * @since 2025-05-22
 */
public interface ITWorkLogService extends IService<TWorkLog> {
    
    /**
     * 检查工作日志是否可以编辑
     * 只有待审核或被驳回的日志可以编辑
     *
     * @param workLogId 工作日志ID
     * @return 是否可以编辑
     */
    boolean canEdit(Long workLogId);

    /**
     * 获取工作日志统计信息
     *
     * @param taskId    任务ID
     * @param userId    用户ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 统计信息
     */
    Map<String, Object> getWorkLogStatistics(Long taskId, Long userId, LocalDate startDate, LocalDate endDate);
}
