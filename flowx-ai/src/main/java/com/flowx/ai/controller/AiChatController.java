package com.flowx.ai.controller;

import com.flowx.ai.dto.ChatRequestDTO;
import com.flowx.ai.dto.ChatResponseDTO;
import com.flowx.ai.service.AiChatService;
import com.flowx.common.core.result.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}