package com.flowx.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * AI conversation entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_conversation")
public class AiConversation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * User ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * Conversation title
     */
    @TableField("conversation_title")
    private String conversationTitle;

    /**
     * Conversation type: chat/approval/report
     */
    @TableField("conversation_type")
    private String conversationType;

    /**
     * Context messages (JSON array)
     */
    @TableField("context_messages")
    private String contextMessages;

    /**
     * Status (0=disabled, 1=active)
     */
    @TableField("status")
    private Integer status;

    /**
     * Last message time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("last_message_time")
    private LocalDateTime lastMessageTime;
}
