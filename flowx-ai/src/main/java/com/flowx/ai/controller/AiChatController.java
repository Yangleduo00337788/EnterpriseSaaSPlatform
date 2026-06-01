package com.flowx.ai.controller;

import com.flowx.ai.dto.ChatRequestDTO;
import com.flowx.ai.dto.ChatResponseDTO;
import com.flowx.ai.service.AiChatService;
import com.flowx.ai.vo.AiConversationVO;
import com.flowx.ai.vo.ChatMessageVO;
import com.flowx.common.core.result.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI chat controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/ai/chat")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;

    /**
     * Send a chat message and get AI response
     *
     * @param request chat request
     * @return chat response
     */
    @PostMapping
    public R<ChatResponseDTO> chat(@Valid @RequestBody ChatRequestDTO request) {
        ChatResponseDTO response = aiChatService.chat(request);
        return R.ok(response);
    }

    /**
     * Get all conversations for a user
     *
     * @param userId user ID
     * @return list of conversations
     */
    @GetMapping("/conversations")
    public R<List<AiConversationVO>> getConversations(@RequestParam("userId") Long userId) {
        List<AiConversationVO> conversations = aiChatService.getConversations(userId);
        return R.ok(conversations);
    }

    /**
     * Get conversation message history
     *
     * @param conversationId conversation ID
     * @return list of chat messages
     */
    @GetMapping("/conversations/{id}/history")
    public R<List<ChatMessageVO>> getConversationHistory(@PathVariable("id") Long conversationId) {
        List<ChatMessageVO> history = aiChatService.getConversationHistory(conversationId);
        return R.ok(history);
    }

    /**
     * Delete a conversation
     *
     * @param conversationId conversation ID
     * @return success response
     */
    @DeleteMapping("/conversations/{id}")
    public R<Void> deleteConversation(@PathVariable("id") Long conversationId) {
        aiChatService.deleteConversation(conversationId);
        return R.ok();
    }

    /**
     * Create a new conversation
     *
     * @param type conversation type (default: chat)
     * @return created conversation
     */
    @PostMapping("/conversations")
    public R<AiConversationVO> createConversation(
            @RequestParam(value = "type", defaultValue = "chat") String type) {
        AiConversationVO conversation = aiChatService.createNewConversation(type);
        return R.ok(conversation);
    }
}
