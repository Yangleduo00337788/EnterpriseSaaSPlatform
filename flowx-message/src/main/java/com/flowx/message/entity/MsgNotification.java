package com.flowx.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * In-app notification entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("msg_notification")
public class MsgNotification extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Target user ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * Notification title
     */
    @TableField("title")
    private String title;

    /**
     * Notification content
     */
    @TableField("content")
    private String content;

    /**
     * Message type: 1=notice, 2=alert, 3=todo
     */
    @TableField("msg_type")
    private Integer msgType;

    /**
     * Read status: 0=unread, 1=read
     */
    @TableField("read_status")
    private Integer readStatus;

    /**
     * Read time
     */
    @TableField("read_time")
    private LocalDateTime readTime;

    /**
     * Business type (for linking to business entity)
     */
    @TableField("business_type")
    private String businessType;

    /**
     * Business ID (for linking to business entity)
     */
    @TableField("business_id")
    private String businessId;
}
