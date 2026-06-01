package com.flowx.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowx.ai.dto.ChatRequestDTO;
import com.flowx.ai.dto.ChatResponseDTO;
import com.flowx.ai.entity.AiConversation;
import com.flowx.ai.mapper.AiConversationMapper;
import com.flowx.ai.service.AiChatService;
import com.flowx.ai.service.provider.AIProvider;
import com.flowx.ai.service.provider.AiProviderFactory;
import com.flowx.ai.vo.AiConversationVO;
import com.flowx.ai.vo.ChatMessageVO;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI chat service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private final AiConversationMapper conversationMapper;
    private final AiProviderFactory providerFactory;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatResponseDTO chat(ChatRequestDTO request) {
        AssertUtil.notNull(request, "请求参数不能为空");
        AssertUtil.notBlank(request.getMessage(), "消息内容不能为空");

        AiConversation conversation;

        // Get or create conversation
        if (request.getConversationId() != null) {
            conversation = conversationMapper.selectById(request.getConversationId());
            AssertUtil.notNull(conversation, ResultCodeEnum.NOT_FOUND.getCode(), "会话不存在");
        } else {
            conversation = createConversation(request.getConversationType());
        }

        // Load context messages
        List<Map<String, String>> context = loadContext(conversation);

        // Call AI provider
        AIProvider provider = providerFactory.getDefaultProvider();
        String aiResponse = provider.chat(request.getMessage(), context);

        // Save user message and AI response to context
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", request.getMessage());

        Map<String, String> assistantMsg = new HashMap<>();
        assistantMsg.put("role", "assistant");
        assistantMsg.put("content", aiResponse);

        context.add(userMsg);
        context.add(assistantMsg);

        // Update conversation
        try {
            conversation.setContextMessages(objectMapper.writeValueAsString(context));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize context messages", e);
        }
        conversation.setLastMessageTime(LocalDateTime.now());

        // Auto-generate title from first message if title is default
        if (conversation.getConversationTitle() == null || conversation.getConversationTitle().equals("新会话")) {
            String title = request.getMessage().length() > 50
                    ? request.getMessage().substring(0, 50) + "..."
                    : request.getMessage();
            conversation.setConversationTitle(title);
        }

        conversationMapper.updateById(conversation);

        return ChatResponseDTO.builder()
                .conversationId(conversation.getId())
                .message(aiResponse)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public List<AiConversationVO> getConversations(Long userId) {
        AssertUtil.notNull(userId, "用户ID不能为空");

        QueryWrapper<AiConversation> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("status", 1)
                .orderByDesc("last_message_time");

        List<AiConversation> conversations = conversationMapper.selectList(wrapper);
        return conversations.stream()
                .map(this::toConversationVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessageVO> getConversationHistory(Long conversationId) {
        AssertUtil.notNull(conversationId, "会话ID不能为空");

        AiConversation conversation = conversationMapper.selectById(conversationId);
        AssertUtil.notNull(conversation, ResultCodeEnum.NOT_FOUND.getCode(), "会话不存在");

        return loadContext(conversation).stream()
                .map(msg -> ChatMessageVO.builder()
                        .role(msg.get("role"))
                        .content(msg.get("content"))
                        .timestamp(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConversation(Long conversationId) {
        AssertUtil.notNull(conversationId, "会话ID不能为空");

        AiConversation conversation = conversationMapper.selectById(conversationId);
        AssertUtil.notNull(conversation, ResultCodeEnum.NOT_FOUND.getCode(), "会话不存在");

        conversationMapper.deleteById(conversationId);
        log.info("Deleted AI conversation: {}", conversationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiConversationVO createNewConversation(String type) {
        AiConversation conversation = createConversation(type);
        conversationMapper.insert(conversation);
        log.info("Created new AI conversation: type={}", type);
        return toConversationVO(conversation);
    }

    /**
     * Create a new conversation entity (not yet persisted)
     */
    private AiConversation createConversation(String type) {
        AiConversation conversation = new AiConversation();
        conversation.setConversationType(type != null ? type : "chat");
        conversation.setConversationTitle("新会话");
        conversation.setContextMessages("[]");
        conversation.setStatus(1);
        conversation.setLastMessageTime(LocalDateTime.now());
        return conversation;
    }

    /**
     * Load context messages from conversation
     */
    private List<Map<String, String>> loadContext(AiConversation conversation) {
        if (conversation.getContextMessages() == null || conversation.getContextMessages().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(
                    conversation.getContextMessages(),
                    new TypeReference<List<Map<String, String>>>() {}
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to parse context messages for conversation {}", conversation.getId(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Convert entity to VO
     */
    private AiConversationVO toConversationVO(AiConversation entity) {
        AiConversationVO vo = new AiConversationVO();
        vo.setId(entity.getId());
        vo.setUserId(entity.getUserId());
        vo.setConversationTitle(entity.getConversationTitle());
        vo.setConversationType(entity.getConversationType());
        vo.setStatus(entity.getStatus());
        vo.setLastMessageTime(entity.getLastMessageTime());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
