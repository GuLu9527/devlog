package com.giao.devlogpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giao.devlogpulse.entity.po.*;
import com.giao.devlogpulse.mapper.*;
import com.giao.devlogpulse.model.dto.NotificationDTO;
import com.giao.devlogpulse.service.INotificationService;
import com.giao.devlogpulse.websocket.NotificationWebSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<TNotificationMapper, TNotification> implements INotificationService {

    private final TNotificationMapper notificationMapper;
    private final TNotificationTemplateMapper templateMapper;
    private final TUserMapper userMapper;
    private final TTaskMapper taskMapper;
    private final TWorkLogMapper workLogMapper;
    private final TProjectMapper projectMapper;

    @Override
    @Transactional
    public boolean sendNotification(NotificationDTO notificationDTO) {
        try {
            TNotification notification = new TNotification();
            BeanUtils.copyProperties(notificationDTO, notification);
            
            // 设置默认值
            if (notification.getLevel() == null) {
                notification.setLevel(TNotification.NotificationLevel.NORMAL);
            }
            if (notification.getIsRead() == null) {
                notification.setIsRead(0);
            }
            if (notification.getIsDeleted() == null) {
                notification.setIsDeleted(0);
            }
            notification.setCreateTime(LocalDateTime.now());
            notification.setUpdateTime(LocalDateTime.now());

            boolean result = save(notification);
            
            if (result) {
                // 发送WebSocket实时通知
                try {
                    NotificationDTO wsNotificationDTO = new NotificationDTO();
                    BeanUtils.copyProperties(notification, wsNotificationDTO);
                    NotificationWebSocket.sendNotificationToUser(notification.getReceiverId(), wsNotificationDTO);
                    
                    // 更新未读数量
                    Long unreadCount = getUnreadCount(notification.getReceiverId());
                    NotificationWebSocket.sendUnreadCountUpdate(notification.getReceiverId(), unreadCount);
                } catch (Exception e) {
                    log.error("发送WebSocket通知失败", e);
                }
                
                log.info("通知发送成功: 接收人ID={}, 标题={}", notification.getReceiverId(), notification.getTitle());
            }
            
            return result;
        } catch (Exception e) {
            log.error("发送通知失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean sendTemplateNotification(String templateType, Long receiverId, Map<String, Object> variables, 
                                           Long senderId, Long relatedId, String relatedType) {
        try {
            // 获取模板
            QueryWrapper<TNotificationTemplate> wrapper = new QueryWrapper<>();
            wrapper.eq("type", templateType).eq("is_active", 1);
            TNotificationTemplate template = templateMapper.selectOne(wrapper);
            
            if (template == null) {
                log.warn("通知模板不存在: {}", templateType);
                return false;
            }

            // 替换模板变量
            String title = replaceTemplateVariables(template.getTitleTemplate(), variables);
            String content = replaceTemplateVariables(template.getContentTemplate(), variables);

            // 创建通知
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setTitle(title);
            notificationDTO.setContent(content);
            notificationDTO.setType(templateType);
            notificationDTO.setLevel("普通");
            notificationDTO.setSenderId(senderId);
            notificationDTO.setReceiverId(receiverId);
            notificationDTO.setRelatedId(relatedId);
            notificationDTO.setRelatedType(relatedType);

            return sendNotification(notificationDTO);
        } catch (Exception e) {
            log.error("发送模板通知失败", e);
            return false;
        }
    }

    @Override
    public IPage<NotificationDTO> getUserNotifications(Long userId, Integer isRead, String type, Integer pageNum, Integer pageSize) {
        Page<TNotification> page = new Page<>(pageNum, pageSize);
        
        QueryWrapper<TNotification> wrapper = new QueryWrapper<>();
        wrapper.eq("receiver_id", userId)
               .eq("is_deleted", 0)
               .orderByDesc("create_time");
        
        if (isRead != null) {
            wrapper.eq("is_read", isRead);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq("type", type);
        }

        IPage<TNotification> notificationPage = page(page, wrapper);
        
        // 转换为DTO
        IPage<NotificationDTO> result = new Page<>(pageNum, pageSize, notificationPage.getTotal());
        List<NotificationDTO> dtoList = notificationPage.getRecords().stream().map(notification -> {
            NotificationDTO dto = new NotificationDTO();
            BeanUtils.copyProperties(notification, dto);
            
            // 获取发送人姓名
            if (notification.getSenderId() != null) {
                TUser sender = userMapper.selectById(notification.getSenderId());
                if (sender != null) {
                    dto.setSenderName(sender.getRealName() != null ? sender.getRealName() : sender.getUsername());
                }
            } else {
                dto.setSenderName("系统");
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        result.setRecords(dtoList);
        return result;
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return notificationMapper.getUnreadCount(userId);
    }

    @Override
    @Transactional
    public boolean markAsRead(Long userId, List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return false;
        }
        
        try {
            String ids = notificationIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            int count = notificationMapper.batchMarkAsRead(userId, ids);
            
            // 如果标记成功，发送未读数量更新
            if (count > 0) {
                try {
                    Long unreadCount = getUnreadCount(userId);
                    NotificationWebSocket.sendUnreadCountUpdate(userId, unreadCount);
                } catch (Exception e) {
                    log.error("发送未读数量更新失败", e);
                }
            }
            
            return count > 0;
        } catch (Exception e) {
            log.error("标记已读失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean markAllAsRead(Long userId) {
        try {
            int count = notificationMapper.markAllAsRead(userId);
            
            // 发送未读数量更新
            if (count >= 0) {
                try {
                    NotificationWebSocket.sendUnreadCountUpdate(userId, 0L); // 全部已读后未读数量为0
                } catch (Exception e) {
                    log.error("发送未读数量更新失败", e);
                }
            }
            
            return count >= 0; // 即使没有未读消息也算成功
        } catch (Exception e) {
            log.error("标记全部已读失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteNotifications(Long userId, List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return false;
        }
        
        try {
            // 先查询用户拥有的通知数量，确保安全性
            QueryWrapper<TNotification> checkWrapper = new QueryWrapper<>();
            checkWrapper.eq("receiver_id", userId)
                       .in("id", notificationIds)
                       .eq("is_deleted", 0);
            long existingCount = count(checkWrapper);
            
            if (existingCount == 0) {
                log.warn("用户{}没有可删除的通知", userId);
                return false;
            }
            
            // 执行软删除
            QueryWrapper<TNotification> updateWrapper = new QueryWrapper<>();
            updateWrapper.eq("receiver_id", userId)
                        .in("id", notificationIds)
                        .eq("is_deleted", 0);
            
            TNotification updateEntity = new TNotification();
            updateEntity.setIsDeleted(1);
            updateEntity.setUpdateTime(LocalDateTime.now());
            
            boolean updateResult = update(updateEntity, updateWrapper);
            int updateCount = updateResult ? (int) existingCount : 0;
            
            // 验证更新数量是否符合预期
            if (updateCount != existingCount) {
                log.warn("删除通知数量不符合预期，期望：{}，实际：{}", existingCount, updateCount);
            }
            
            return updateResult;
        } catch (Exception e) {
            log.error("删除通知失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public int batchSendNotification(List<Long> receiverIds, NotificationDTO notificationDTO) {
        int successCount = 0;
        for (Long receiverId : receiverIds) {
            NotificationDTO dto = new NotificationDTO();
            BeanUtils.copyProperties(notificationDTO, dto);
            dto.setReceiverId(receiverId);
            
            if (sendNotification(dto)) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public void sendTaskAssignNotification(Long taskId, Long assigneeId, Long assignerId) {
        try {
            TTask task = taskMapper.selectById(taskId);
            TUser assigner = userMapper.selectById(assignerId);
            
            if (task != null && assigner != null) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("taskTitle", task.getTitle());
                variables.put("assignerName", assigner.getRealName() != null ? assigner.getRealName() : assigner.getUsername());
                variables.put("deadline", task.getDeadline() != null ? task.getDeadline().toString() : "未设置");
                
                sendTemplateNotification("TASK_ASSIGN", assigneeId, variables, assignerId, taskId, "task");
            }
        } catch (Exception e) {
            log.error("发送任务分配通知失败", e);
        }
    }

    @Override
    public void sendTaskDeadlineNotification(Long taskId, Long assigneeId) {
        try {
            TTask task = taskMapper.selectById(taskId);
            
            if (task != null) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("taskTitle", task.getTitle());
                variables.put("deadline", task.getDeadline() != null ? task.getDeadline().toString() : "未设置");
                
                sendTemplateNotification("TASK_DEADLINE", assigneeId, variables, null, taskId, "task");
            }
        } catch (Exception e) {
            log.error("发送任务截止通知失败", e);
        }
    }

    @Override
    public void sendWorklogReviewNotification(Long worklogId, Long reviewerId, Long submitterId) {
        try {
            TWorkLog workLog = workLogMapper.selectById(worklogId);
            TUser submitter = userMapper.selectById(submitterId);
            
            if (workLog != null && submitter != null) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("submitterName", submitter.getRealName() != null ? submitter.getRealName() : submitter.getUsername());
                variables.put("logDate", workLog.getLogDate().toString());
                variables.put("content", workLog.getContent().length() > 50 ? 
                    workLog.getContent().substring(0, 50) + "..." : workLog.getContent());
                
                sendTemplateNotification("WORKLOG_REVIEW", reviewerId, variables, submitterId, worklogId, "worklog");
            }
        } catch (Exception e) {
            log.error("发送工作日志审核通知失败", e);
        }
    }

    @Override
    public void sendProjectUpdateNotification(Long projectId, List<Long> receiverIds, Long updaterId) {
        try {
            TProject project = projectMapper.selectById(projectId);
            TUser updater = userMapper.selectById(updaterId);
            
            if (project != null && updater != null) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("projectName", project.getName());
                variables.put("newStatus", project.getStatus());
                variables.put("updaterName", updater.getRealName() != null ? updater.getRealName() : updater.getUsername());
                
                for (Long receiverId : receiverIds) {
                    sendTemplateNotification("PROJECT_UPDATE", receiverId, variables, updaterId, projectId, "project");
                }
            }
        } catch (Exception e) {
            log.error("发送项目更新通知失败", e);
        }
    }

    /**
     * 替换模板变量
     * 支持格式：{variableName}、{variableName:defaultValue}、{variableName|format}
     */
    private String replaceTemplateVariables(String template, Map<String, Object> variables) {
        if (template == null || variables == null) {
            return template;
        }
        
        String result = template;
        
        // 正则匹配 {变量名} 或 {变量名:默认值} 或 {变量名|格式}
        String pattern = "\\{([^}]+)\\}";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(template);
        
        while (matcher.find()) {
            String fullMatch = matcher.group(0); // 完整匹配 {xxx}
            String variableExpression = matcher.group(1); // 变量表达式 xxx
            
            String variableName;
            String defaultValue = "";
            String format = "";
            
            // 解析变量表达式
            if (variableExpression.contains(":")) {
                // 格式：{变量名:默认值}
                String[] parts = variableExpression.split(":", 2);
                variableName = parts[0].trim();
                defaultValue = parts[1].trim();
            } else if (variableExpression.contains("|")) {
                // 格式：{变量名|格式}
                String[] parts = variableExpression.split("\\|", 2);
                variableName = parts[0].trim();
                format = parts[1].trim();
            } else {
                // 格式：{变量名}
                variableName = variableExpression.trim();
            }
            
            // 获取变量值
            Object variableValue = variables.get(variableName);
            String replacementValue;
            
            if (variableValue == null) {
                replacementValue = defaultValue;
            } else {
                replacementValue = formatVariableValue(variableValue, format);
            }
            
            // 替换模板中的占位符
            result = result.replace(fullMatch, replacementValue);
        }
        
        return result;
    }
    
    /**
     * 格式化变量值
     */
    private String formatVariableValue(Object value, String format) {
        if (value == null) {
            return "";
        }
        
        String stringValue = value.toString();
        
        if (format.isEmpty()) {
            return stringValue;
        }
        
        try {
            switch (format.toLowerCase()) {
                case "upper":
                    return stringValue.toUpperCase();
                case "lower":
                    return stringValue.toLowerCase();
                case "capitalize":
                    return stringValue.substring(0, 1).toUpperCase() + stringValue.substring(1).toLowerCase();
                case "truncate10":
                    return stringValue.length() > 10 ? stringValue.substring(0, 10) + "..." : stringValue;
                case "truncate20":
                    return stringValue.length() > 20 ? stringValue.substring(0, 20) + "..." : stringValue;
                case "date":
                    if (value instanceof java.time.LocalDateTime) {
                        java.time.format.DateTimeFormatter formatter = 
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        return ((java.time.LocalDateTime) value).format(formatter);
                    } else if (value instanceof java.time.LocalDate) {
                        java.time.format.DateTimeFormatter formatter = 
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        return ((java.time.LocalDate) value).format(formatter);
                    }
                    return stringValue;
                default:
                    return stringValue;
            }
        } catch (Exception e) {
            log.warn("格式化变量值失败: value={}, format={}", value, format, e);
            return stringValue;
        }
    }

    /**
     * 发送工作日志审核结果通知（给提交人）
     */
    public void sendWorklogReviewResultNotification(Long worklogId, TWorkLog.ReviewStatus reviewStatus, String reviewComment, Long reviewerId) {
        try {
            TWorkLog workLog = workLogMapper.selectById(worklogId);
            TUser reviewer = userMapper.selectById(reviewerId);
            
            if (workLog != null && reviewer != null) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("reviewerName", reviewer.getRealName() != null ? reviewer.getRealName() : reviewer.getUsername());
                variables.put("logDate", workLog.getLogDate());
                variables.put("hours", workLog.getHours());
                variables.put("reviewStatus", reviewStatus.name());
                variables.put("reviewComment", reviewComment != null ? reviewComment : "无");
                variables.put("statusText", reviewStatus == TWorkLog.ReviewStatus.已通过 ? "通过" : "拒绝");
                
                String templateType = reviewStatus == TWorkLog.ReviewStatus.已通过 ? 
                    "WORKLOG_APPROVED" : "WORKLOG_REJECTED";
                
                sendTemplateNotification(templateType, workLog.getUserId(), variables, reviewerId, worklogId, "worklog");
            }
        } catch (Exception e) {
            log.error("发送工作日志审核结果通知失败", e);
        }
    }

    /**
     * 发送任务进度更新通知
     */
    public void sendTaskProgressUpdateNotification(Long taskId, Integer oldProgress, Integer newProgress, String oldStatus, String newStatus) {
        try {
            TTask task = taskMapper.selectById(taskId);
            
            if (task != null && task.getAssigneeId() != null) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("taskTitle", task.getTitle() != null ? task.getTitle() : "");
                variables.put("oldProgress", oldProgress != null ? oldProgress.toString() : "0");
                variables.put("newProgress", newProgress != null ? newProgress.toString() : "0");
                variables.put("oldStatus", oldStatus != null ? oldStatus : "");
                variables.put("newStatus", newStatus != null ? newStatus : "");
                variables.put("actualHours", task.getActualHours() != null ? task.getActualHours().toString() : "0");
                
                sendTemplateNotification("TASK_PROGRESS_UPDATE", task.getAssigneeId(), variables, null, taskId, "task");
            }
        } catch (Exception e) {
            log.error("发送任务进度更新通知失败", e);
        }
    }
}