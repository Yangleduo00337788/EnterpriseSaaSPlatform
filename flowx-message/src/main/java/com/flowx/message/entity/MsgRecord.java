package com.flowx.message.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * External message sending record entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("msg_record")
public class MsgRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Template code used for this message
     */
    @Column("template_code")
    private String templateCode;

    /**
     * Receiver address (email, phone, user id, etc.)
     */
    @Column("receiver")
    private String receiver;

    /**
     * Receiver type (email, phone, userId, etc.)
     */
    @Column("receiver_type")
    private String receiverType;

    /**
     * Send channel: email/sms/wechat_work/dingtalk
     */
    @Column("channel")
    private String channel;

    /**
     * Actual sent content (after template rendering)
     */
    @Column("content")
    private String content;

    /**
     * Send status: 0=pending, 1=success, 2=failed
     */
    @Column("send_status")
    private Integer sendStatus;

    /**
     * Send time
     */
    @Column("send_time")
    private LocalDateTime sendTime;

    /**
     * Error message if send failed
     */
    @Column("error_msg")
    private String errorMsg;

    /**
     * Retry count
     */
    @Column("retry_count")
    private Integer retryCount;
}
