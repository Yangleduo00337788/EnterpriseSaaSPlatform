package com.flowx.message.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * External message record view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class MsgRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Record ID
     */
    private Long id;

    /**
     * Template code
     */
    private String templateCode;

    /**
     * Receiver address
     */
    private String receiver;

    /**
     * Receiver type
     */
    private String receiverType;

    /**
     * Send channel
     */
    private String channel;

    /**
     * Sent content
     */
    private String content;

    /**
     * Send status: 0=pending, 1=success, 2=failed
     */
    private Integer sendStatus;

    /**
     * Send time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;

    /**
     * Error message
     */
    private String errorMsg;

    /**
     * Retry count
     */
    private Integer retryCount;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
