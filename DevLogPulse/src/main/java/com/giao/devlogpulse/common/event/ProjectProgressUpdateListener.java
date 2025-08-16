package com.giao.devlogpulse.common.event;

import com.giao.devlogpulse.service.ITProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 项目进度更新监听器
 * 监听任务进度更新事件，自动触发项目进度的重新计算
 * 
 * @author giao
 * @since 2025-08-06
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectProgressUpdateListener {
    
    private final ITProjectService projectService;
    
    /**
     * 处理任务进度更新事件
     * 异步处理以避免影响主流程性能
     */
    @Async
    @EventListener
    public void handleTaskProgressUpdate(TaskProgressUpdateEvent event) {
        try {
            // 只有当任务关联了项目时才处理
            if (event.getProjectId() != null) {
                log.info("收到任务进度更新事件 - 任务ID: {}, 项目ID: {}, 事件类型: {}, 进度变化: {}% -> {}%", 
                    event.getTaskId(), event.getProjectId(), event.getEventType(), 
                    event.getOldProgress(), event.getNewProgress());
                
                // 触发项目进度更新
                boolean success = projectService.updateProjectProgress(event.getProjectId());
                
                if (success) {
                    log.info("项目进度更新成功 - 项目ID: {}, 触发任务ID: {}", 
                        event.getProjectId(), event.getTaskId());
                } else {
                    log.warn("项目进度更新失败 - 项目ID: {}, 触发任务ID: {}", 
                        event.getProjectId(), event.getTaskId());
                }
            } else {
                log.debug("任务无关联项目，跳过项目进度更新 - 任务ID: {}", event.getTaskId());
            }
            
        } catch (Exception e) {
            log.error("处理任务进度更新事件失败 - 任务ID: {}, 项目ID: {}", 
                event.getTaskId(), event.getProjectId(), e);
        }
    }
}