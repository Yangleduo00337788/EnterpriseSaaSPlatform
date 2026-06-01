package com.flowx.ai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Chat response DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Conversation ID
     */
    private Long conversationId;

    /**
     * AI response message
     */
    private String message;

    /**
     * Response timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
