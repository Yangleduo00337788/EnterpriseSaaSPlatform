package com.flowx.ai.service;

import com.flowx.ai.dto.ChatRequestDTO;
import com.flowx.ai.dto.ChatResponseDTO;
import com.flowx.ai.vo.AiConversationVO;
import com.flowx.ai.vo.ChatMessageVO;

import java.util.List;

/**
 * AI chat service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface AiChatService {

    /**
     * Send a chat message and get AI response
     *
     * @param request chat request DTO
     * @return chat response DTO
     */
    ChatResponseDTO chat(ChatRequestDTO request);

    /**
     * Get all conversations for a user
     *
     * @param userId user ID
     * @return list of conversation VOs
     */
    List<AiConversationVO> getConversations(Long userId);

    /**
     * Get conversation message history
     *
     * @param conversationId conversation ID
     * @return list of chat message VOs
     */
    List<ChatMessageVO> getConversationHistory(Long conversationId);

    /**
     * Delete a conversation
     *
     * @param conversationId conversation ID
     */
    void deleteConversation(Long conversationId);

    /**
     * Create a new conversation
     *
     * @param type conversation type: chat/approval/report
     * @return created conversation VO
     */
    AiConversationVO createNewConversation(String type);
}
