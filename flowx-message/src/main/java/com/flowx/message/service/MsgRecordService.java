package com.flowx.message.service;

import com.flowx.message.dto.SendExternalDTO;

/**
 * External message record service interface (internal, not exposed via API)
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface MsgRecordService {

    /**
     * Send external message via appropriate channel
     *
     * @param dto external message data
     */
    void sendExternalMessage(SendExternalDTO dto);

    /**
     * Retry failed messages (called by scheduler)
     */
    void retryFailedMessages();

    /**
     * Send email
     *
     * @param receiver receiver email address
     * @param subject  email subject
     * @param content  email content (HTML)
     */
    void sendEmail(String receiver, String subject, String content);

    /**
     * Send SMS
     *
     * @param receiver receiver phone number
     * @param content  SMS content
     */
    void sendSms(String receiver, String content);

    /**
     * Send WeChat Work message via webhook
     *
     * @param webhookUrl webhook URL
     * @param content    message content
     */
    void sendWechatWork(String webhookUrl, String content);

    /**
     * Send DingTalk message via webhook
     *
     * @param webhookUrl webhook URL
     * @param content    message content
     */
    void sendDingTalk(String webhookUrl, String content);
}
