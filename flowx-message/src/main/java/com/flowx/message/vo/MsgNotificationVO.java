package com.flowx.message.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * In-app notification view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class MsgNotificationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Notification ID
     */
    private Long id;

    /**
     * Target user ID
     */
    private Long userId;

    /**
     * Notification title
     */
    private String title;

    /**
     * Notification content
     */
    private String content;

    /**
     * Message type: 1=notice, 2=alert, 3=todo
     */
    private Integer msgType;

    /**
     * Read status: 0=unread, 1=read
     */
    private Integer readStatus;

    /**
     * Read time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;

    /**
     * Business type
     */
    private String businessType;

    /**
     * Business ID
     */
    private String businessId;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
