package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giao.devlogpulse.entity.po.TWorkLog;
import com.giao.devlogpulse.mapper.TWorkLogMapper;
import com.giao.devlogpulse.service.ITWorkLogService;
import com.giao.devlogpulse.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工作日志表 服务实现类
 * </p>
 *
 * @author giao
 * @since 2025-05-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TWorkLogServiceImpl extends ServiceImpl<TWorkLogMapper, TWorkLog> implements ITWorkLogService {

    private final INotificationService notificationService;

    @Override
    public boolean save(TWorkLog entity) {
        boolean result = super.save(entity);
        
        // 如果保存成功且设置了审核人，发送工作日志审核通知
        if (result && entity.getReviewerId() != null && entity.getUserId() != null) {
            try {
                notificationService.sendWorklogReviewNotification(entity.getId(), entity.getReviewerId(), entity.getUserId());
            } catch (Exception e) {
                log.error("发送工作日志审核通知失败", e);
            }
        }
        
        return result;
    }

    @Override
    public boolean updateById(TWorkLog entity) {
        TWorkLog oldWorkLog = getById(entity.getId());
        boolean result = super.updateById(entity);
        
        // 如果更新成功，检查审核状态是否发生变化
        if (result && oldWorkLog != null) {
            try {
                // 如果审核人发生变化，发送通知
                if (entity.getReviewerId() != null && 
                    !entity.getReviewerId().equals(oldWorkLog.getReviewerId())) {
                    notificationService.sendWorklogReviewNotification(entity.getId(), entity.getReviewerId(), entity.getUserId());
                }
            } catch (Exception e) {
                log.error("发送工作日志审核通知失败", e);
            }
        }
        
        return result;
    }

    @Override
    public boolean canEdit(Long workLogId) {
        TWorkLog workLog = this.getById(workLogId);
        return workLog != null && 
               (workLog.getReviewStatus().equals("PENDING") || 
                workLog.getReviewStatus().equals("REJECTED"));
    }

    @Override
    public Map<String, Object> getWorkLogStatistics(Long taskId, Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<TWorkLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TWorkLog::getIsDeleted, 0)
                   .eq(TWorkLog::getReviewStatus, "APPROVED");
        
        if (taskId != null) {
            queryWrapper.eq(TWorkLog::getTaskId, taskId);
        }
        if (userId != null) {
            queryWrapper.eq(TWorkLog::getUserId, userId);
        }
        if (startDate != null) {
            queryWrapper.ge(TWorkLog::getLogDate, startDate);
        }
        if (endDate != null) {
            queryWrapper.le(TWorkLog::getLogDate, endDate);
        }
        
        List<TWorkLog> workLogs = this.list(queryWrapper);
        
        // 统计总工时
        BigDecimal totalHours = workLogs.stream()
                .map(TWorkLog::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 统计日志数量
        long totalCount = workLogs.size();
        
        // 统计每个任务的工时
        Map<Long, BigDecimal> taskHours = new HashMap<>();
        workLogs.forEach(log -> {
            taskHours.merge(log.getTaskId(), log.getHours(), BigDecimal::add);
        });
        
        // 统计每个用户的工时
        Map<Long, BigDecimal> userHours = new HashMap<>();
        workLogs.forEach(log -> {
            userHours.merge(log.getUserId(), log.getHours(), BigDecimal::add);
        });
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalHours", totalHours);
        statistics.put("totalCount", totalCount);
        statistics.put("taskHours", taskHours);
        statistics.put("userHours", userHours);
        
        return statistics;
    }
}
