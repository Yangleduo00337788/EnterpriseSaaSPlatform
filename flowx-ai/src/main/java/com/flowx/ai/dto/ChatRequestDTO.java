package com.flowx.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * Chat request DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class ChatRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Conversation ID (null for new conversation)
     */
    private Long conversationId;

    /**
     * User message
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;

    /**
     * Conversation type: chat/approval/report, default "chat"
     */
    private String conversationType = "chat";
}
