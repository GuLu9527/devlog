package com.giao.devlogpulse.service;

import com.giao.devlogpulse.entity.po.TNotification;
import com.giao.devlogpulse.model.dto.NotificationDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 通知服务接口
 */
public interface INotificationService extends IService<TNotification> {

    /**
     * 发送通知
     * @param notificationDTO 通知信息
     * @return 是否发送成功
     */
    boolean sendNotification(NotificationDTO notificationDTO);

    /**
     * 发送模板通知
     * @param templateType 模板类型
     * @param receiverId 接收人ID
     * @param variables 模板变量
     * @param senderId 发送人ID(可为空，系统消息)
     * @param relatedId 关联业务ID
     * @param relatedType 关联业务类型
     * @return 是否发送成功
     */
    boolean sendTemplateNotification(String templateType, Long receiverId, Map<String, Object> variables, 
                                   Long senderId, Long relatedId, String relatedType);

    /**
     * 获取用户通知列表(分页)
     * @param userId 用户ID
     * @param isRead 是否已读(null表示全部)
     * @param type 通知类型(null表示全部)
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 通知列表
     */
    IPage<NotificationDTO> getUserNotifications(Long userId, Integer isRead, String type, Integer pageNum, Integer pageSize);

    /**
     * 获取用户未读通知数量
     * @param userId 用户ID
     * @return 未读数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 标记为已读
     * @param userId 用户ID
     * @param notificationIds 通知ID列表
     * @return 是否成功
     */
    boolean markAsRead(Long userId, List<Long> notificationIds);

    /**
     * 标记所有为已读
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAllAsRead(Long userId);

    /**
     * 删除通知
     * @param userId 用户ID
     * @param notificationIds 通知ID列表
     * @return 是否成功
     */
    boolean deleteNotifications(Long userId, List<Long> notificationIds);

    /**
     * 批量发送通知(群发)
     * @param receiverIds 接收人ID列表
     * @param notificationDTO 通知信息
     * @return 发送成功的数量
     */
    int batchSendNotification(List<Long> receiverIds, NotificationDTO notificationDTO);

    /**
     * 任务分配通知
     * @param taskId 任务ID
     * @param assigneeId 负责人ID
     * @param assignerId 分配人ID
     */
    void sendTaskAssignNotification(Long taskId, Long assigneeId, Long assignerId);

    /**
     * 任务截止提醒
     * @param taskId 任务ID
     * @param assigneeId 负责人ID
     */
    void sendTaskDeadlineNotification(Long taskId, Long assigneeId);

    /**
     * 工作日志审核提醒
     * @param worklogId 日志ID
     * @param reviewerId 审核人ID
     * @param submitterId 提交人ID
     */
    void sendWorklogReviewNotification(Long worklogId, Long reviewerId, Long submitterId);

    /**
     * 项目状态更新通知
     * @param projectId 项目ID
     * @param receiverIds 接收人ID列表
     * @param updaterId 更新人ID
     */
    void sendProjectUpdateNotification(Long projectId, List<Long> receiverIds, Long updaterId);
}