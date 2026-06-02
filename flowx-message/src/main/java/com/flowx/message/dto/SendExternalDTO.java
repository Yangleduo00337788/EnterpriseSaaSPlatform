package com.flowx.message.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO for sending external message (email, SMS, WeChat Work, DingTalk)
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class SendExternalDTO implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * Template code
     */
    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    /**
     * Receiver address (email, phone, webhook, etc.)
     */
    @NotBlank(message = "接收者不能为空")
    private String receiver;

    /**
     * Receiver type (email, phone, userId, etc.)
     */
    private String receiverType;

    /**
     * Template parameters for rendering
     */
    private Map<String, String> params;

    /**
     * Send channel: email/sms/wechat_work/dingtalk
     */
    @NotBlank(message = "发送渠道不能为空")
    private String channel;
}
