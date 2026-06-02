package com.flowx.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for sending in-app notification
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class SendNotificationDTO implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * Target user ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * Notification title
     */
    @NotBlank(message = "通知标题不能为空")
    private String title;

    /**
     * Notification content
     */
    @NotBlank(message = "通知内容不能为空")
    private String content;

    /**
     * Message type: 1=notice, 2=alert, 3=todo
     */
    @NotNull(message = "消息类型不能为空")
    private Integer msgType;

    /**
     * Business type (for linking to business entity)
     */
    private String businessType;

    /**
     * Business ID (for linking to business entity)
     */
    private String businessId;
}
