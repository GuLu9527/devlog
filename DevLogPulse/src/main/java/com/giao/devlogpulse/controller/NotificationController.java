package com.giao.devlogpulse.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.giao.devlogpulse.common.api.Result;
import com.giao.devlogpulse.model.dto.NotificationDTO;
import com.giao.devlogpulse.model.dto.BatchNotificationDTO;
import com.giao.devlogpulse.service.INotificationService;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.giao.devlogpulse.common.exception.BusinessException;

@Slf4j
@Tag(name = "消息通知", description = "消息通知相关接口")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final INotificationService notificationService;

    @Operation(summary = "获取用户通知列表")
    @GetMapping("/list")
    @SaCheckPermission("notification:list")
    public Result<IPage<NotificationDTO>> getUserNotifications(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "是否已读(0未读 1已读)") @RequestParam(required = false) Integer isRead,
            @Parameter(description = "通知类型") @RequestParam(required = false) String type,
            HttpServletRequest request
    ) {
        // 从SaToken中获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        IPage<NotificationDTO> page = notificationService.getUserNotifications(userId, isRead, type, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    @SaCheckPermission("notification:read")
    public Result<Map<String, Long>> getUnreadCount(HttpServletRequest request) {
        // 从SaToken中获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        Long count = notificationService.getUnreadCount(userId);
        Map<String, Long> result = new HashMap<>();
        result.put("unreadCount", count);
        return Result.success(result);
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/mark-read")
    public Result<String> markAsRead(
            @Parameter(description = "通知ID列表") @RequestBody List<Long> notificationIds,
            HttpServletRequest request
    ) {
        // 从SaToken中获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        boolean success = notificationService.markAsRead(userId, notificationIds);
        return success ? Result.success("标记已读成功") : Result.failed("标记已读失败");
    }

    @Operation(summary = "标记所有通知为已读")
    @PutMapping("/mark-all-read")
    public Result<String> markAllAsRead(HttpServletRequest request) {
        // 从SaToken中获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        boolean success = notificationService.markAllAsRead(userId);
        return success ? Result.success("全部标记已读成功") : Result.failed("标记失败");
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/delete")
    public Result<String> deleteNotifications(
            @Parameter(description = "通知ID列表") @RequestBody List<Long> notificationIds,
            HttpServletRequest request
    ) {
        // 从SaToken中获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        boolean success = notificationService.deleteNotifications(userId, notificationIds);
        return success ? Result.success("删除成功") : Result.failed("删除失败");
    }

    @Operation(summary = "发送通知")
    @PostMapping("/send")
    public Result<String> sendNotification(@Valid @RequestBody NotificationDTO notificationDTO) {
        boolean success = notificationService.sendNotification(notificationDTO);
        return success ? Result.success("发送成功") : Result.failed("发送失败");
    }

    @Operation(summary = "批量发送通知")
    @PostMapping("/batch-send")
    @SaCheckPermission("notification:send")
    public Result<String> batchSendNotification(
            @RequestBody BatchNotificationDTO batchNotificationDTO
    ) {
        try {
            log.info("接收到批量发送通知请求，数据：{}", batchNotificationDTO);
            
            // 参数验证
            if (batchNotificationDTO == null) {
                return Result.failed("请求数据不能为空");
            }
            if (batchNotificationDTO.getReceiverIds() == null || batchNotificationDTO.getReceiverIds().isEmpty()) {
                return Result.failed("接收人ID列表不能为空");
            }
            if (batchNotificationDTO.getNotification() == null) {
                return Result.failed("通知信息不能为空");
            }
            
            // 设置默认值
            NotificationDTO notification = batchNotificationDTO.getNotification();
            if (notification.getLevel() == null || notification.getLevel().isEmpty()) {
                notification.setLevel("普通");
            }
            
            log.info("开始批量发送通知，接收人数量：{}，通知标题：{}", 
                    batchNotificationDTO.getReceiverIds().size(), 
                    notification.getTitle());
            
            int successCount = notificationService.batchSendNotification(
                    batchNotificationDTO.getReceiverIds(), 
                    notification
            );
            
            log.info("批量发送通知完成，成功发送：{} 条", successCount);
            return Result.success("成功发送 " + successCount + " 条通知");
            
        } catch (Exception e) {
            log.error("批量发送通知失败", e);
            return Result.failed("发送失败：" + e.getMessage());
        }
    }

    @Operation(summary = "发送测试通知")
    @PostMapping("/test")
    public Result<String> sendTestNotification(
            @Parameter(description = "接收人ID") @RequestParam Long receiverId
    ) {
        NotificationDTO notification = new NotificationDTO();
        notification.setTitle("测试通知");
        notification.setContent("这是一条测试通知消息，用于验证通知系统是否正常工作。");
        notification.setType("SYSTEM_MESSAGE");
        notification.setLevel("普通");
        notification.setReceiverId(receiverId);
        
        boolean success = notificationService.sendNotification(notification);
        return success ? Result.success("测试通知发送成功") : Result.failed("测试通知发送失败");
    }
}