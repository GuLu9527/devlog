package com.giao.devlogpulse.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量通知发送DTO
 */
@Data
@Schema(description = "批量通知发送请求")
public class BatchNotificationDTO {

    @Schema(description = "接收人ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "接收人ID列表不能为空")
    private List<Long> receiverIds;

    @Schema(description = "通知信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "通知信息不能为空")
    @Valid
    private NotificationDTO notification;
}