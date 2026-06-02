package com.flowx.ai.controller;

import com.flowx.ai.service.AiChatService;
import com.flowx.ai.vo.AiConversationVO;
import com.flowx.ai.vo.ChatMessageVO;
import com.flowx.common.core.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI conversation management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/ai/conversations")
@RequiredArgsConstructor
public class AiConversationController {

    private final AiChatService aiChatService;

    /**
     * Get all conversations for a user
     *
     * @param userId user ID
     * @return list of conversations
     */
    @GetMapping
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
    @GetMapping("/{id}/history")
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
    @DeleteMapping("/{id}")
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
    @PostMapping
    public R<AiConversationVO> createConversation(
            @RequestParam(value = "type", defaultValue = "chat") String type) {
        AiConversationVO conversation = aiChatService.createNewConversation(type);
        return R.ok(conversation);
    }
}