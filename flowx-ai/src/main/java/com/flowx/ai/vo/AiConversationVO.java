package com.flowx.ai.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI conversation view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class AiConversationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Conversation ID
     */
    private Long id;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Conversation title
     */
    private String conversationTitle;

    /**
     * Conversation type: chat/approval/report
     */
    private String conversationType;

    /**
     * Status (0=disabled, 1=active)
     */
    private Integer status;

    /**
     * Last message time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageTime;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
