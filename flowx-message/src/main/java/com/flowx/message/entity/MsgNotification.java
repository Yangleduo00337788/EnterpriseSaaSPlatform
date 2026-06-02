package com.flowx.message.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
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
@Table("msg_notification")
public class MsgNotification extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Target user ID
     */
    @Column("user_id")
    private Long userId;

    /**
     * Notification title
     */
    @Column("title")
    private String title;

    /**
     * Notification content
     */
    @Column("content")
    private String content;

    /**
     * Message type: 1=notice, 2=alert, 3=todo
     */
    @Column("msg_type")
    private Integer msgType;

    /**
     * Read status: 0=unread, 1=read
     */
    @Column("read_status")
    private Integer readStatus;

    /**
     * Read time
     */
    @Column("read_time")
    private LocalDateTime readTime;

    /**
     * Business type (for linking to business entity)
     */
    @Column("business_type")
    private String businessType;

    /**
     * Business ID (for linking to business entity)
     */
    @Column("business_id")
    private String businessId;
}
