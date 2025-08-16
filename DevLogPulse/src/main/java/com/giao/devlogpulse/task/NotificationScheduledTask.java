package com.giao.devlogpulse.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.giao.devlogpulse.entity.po.TTask;
import com.giao.devlogpulse.mapper.TTaskMapper;
import com.giao.devlogpulse.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知相关定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduledTask {

    private final TTaskMapper taskMapper;
    private final INotificationService notificationService;

    /**
     * 检查即将到期的任务，每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * ?") // 每小时的整点执行
    public void checkTaskDeadlines() {
        log.info("开始检查任务截止时间...");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime tomorrow = now.plusDays(1);
            
            // 查询24小时内截止且未完成的任务
            QueryWrapper<TTask> wrapper = new QueryWrapper<>();
            wrapper.eq("is_deleted", 0)
                   .ne("status", "已完成")
                   .ne("status", "已取消")
                   .between("deadline", now, tomorrow)
                   .isNotNull("assignee_id");
            
            List<TTask> urgentTasks = taskMapper.selectList(wrapper);
            
            log.info("发现{}个即将到期的任务", urgentTasks.size());
            
            // 为每个即将到期的任务发送通知
            for (TTask task : urgentTasks) {
                try {
                    notificationService.sendTaskDeadlineNotification(task.getId(), task.getAssigneeId());
                    log.info("已发送任务截止通知，任务ID：{}，负责人ID：{}", task.getId(), task.getAssigneeId());
                } catch (Exception e) {
                    log.error("发送任务截止通知失败，任务ID：{}", task.getId(), e);
                }
            }
            
        } catch (Exception e) {
            log.error("检查任务截止时间失败", e);
        }
        
        log.info("任务截止时间检查完成");
    }

    /**
     * 检查已逾期的任务，每天上午9点执行
     */
    @Scheduled(cron = "0 0 9 * * ?") // 每天上午9点执行
    public void checkOverdueTasks() {
        log.info("开始检查逾期任务...");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 查询已逾期且未完成的任务
            QueryWrapper<TTask> wrapper = new QueryWrapper<>();
            wrapper.eq("is_deleted", 0)
                   .ne("status", "已完成")
                   .ne("status", "已取消")
                   .lt("deadline", now)
                   .isNotNull("assignee_id");
            
            List<TTask> overdueTasks = taskMapper.selectList(wrapper);
            
            log.info("发现{}个逾期任务", overdueTasks.size());
            
            // 为每个逾期任务发送通知（可以考虑发送给负责人和创建人）
            for (TTask task : overdueTasks) {
                try {
                    // 发送给负责人
                    notificationService.sendTaskDeadlineNotification(task.getId(), task.getAssigneeId());
                    
                    // 如果创建人不是负责人，也发送给创建人
                    if (task.getCreatorId() != null && !task.getCreatorId().equals(task.getAssigneeId())) {
                        notificationService.sendTaskDeadlineNotification(task.getId(), task.getCreatorId());
                    }
                    
                    log.info("已发送逾期任务通知，任务ID：{}", task.getId());
                } catch (Exception e) {
                    log.error("发送逾期任务通知失败，任务ID：{}", task.getId(), e);
                }
            }
            
        } catch (Exception e) {
            log.error("检查逾期任务失败", e);
        }
        
        log.info("逾期任务检查完成");
    }

    /**
     * 清理过期通知，每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void cleanupExpiredNotifications() {
        log.info("开始清理过期通知...");
        
        try {
            // 这里可以添加清理30天前的已读通知等逻辑
            // 目前暂时保留所有通知，后续可根据需要实现
            
            log.info("过期通知清理完成");
        } catch (Exception e) {
            log.error("清理过期通知失败", e);
        }
    }
}