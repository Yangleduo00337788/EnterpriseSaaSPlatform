package com.flowx.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("msg_record")
public class MsgRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Template code used for this message
     */
    @TableField("template_code")
    private String templateCode;

    /**
     * Receiver address (email, phone, user id, etc.)
     */
    @TableField("receiver")
    private String receiver;

    /**
     * Receiver type (email, phone, userId, etc.)
     */
    @TableField("receiver_type")
    private String receiverType;

    /**
     * Send channel: email/sms/wechat_work/dingtalk
     */
    @TableField("channel")
    private String channel;

    /**
     * Actual sent content (after template rendering)
     */
    @TableField("content")
    private String content;

    /**
     * Send status: 0=pending, 1=success, 2=failed
     */
    @TableField("send_status")
    private Integer sendStatus;

    /**
     * Send time
     */
    @TableField("send_time")
    private LocalDateTime sendTime;

    /**
     * Error message if send failed
     */
    @TableField("error_msg")
    private String errorMsg;

    /**
     * Retry count
     */
    @TableField("retry_count")
    private Integer retryCount;
}
