package com.flowx.message.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.util.AssertUtil;
import com.flowx.message.config.NotificationConfig;
import com.flowx.message.dto.SendExternalDTO;
import com.flowx.message.entity.MsgRecord;
import com.flowx.message.entity.MsgTemplate;
import com.flowx.message.mapper.MsgRecordMapper;
import com.flowx.message.mapper.MsgTemplateMapper;
import com.flowx.message.service.MsgRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * External message record service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MsgRecordServiceImpl implements MsgRecordService {

    private final MsgRecordMapper recordMapper;
    private final MsgTemplateMapper templateMapper;
    private final NotificationConfig notificationConfig;
    private final JavaMailSender javaMailSender;
    private final WebClient.Builder webClientBuilder;

    /**
     * Maximum retry count for failed messages
     */
    private static final int MAX_RETRY_COUNT = 3;

    @Override
    public void sendExternalMessage(SendExternalDTO dto) {
        AssertUtil.notNull(dto, "外部消息数据不能为空");
        AssertUtil.notBlank(dto.getTemplateCode(), "模板编码不能为空");
        AssertUtil.notBlank(dto.getReceiver(), "接收者不能为空");
        AssertUtil.notBlank(dto.getChannel(), "发送渠道不能为空");

        // Load template
        MsgTemplate template = templateMapper.selectByTemplateCode(dto.getTemplateCode());
        if (template == null) {
            throw new BizException("消息模板不存在: " + dto.getTemplateCode());
        }

        // Render content from template
        String subject = renderTemplate(template.getTitleTemplate(), dto.getParams());
        String content = renderTemplate(template.getContentTemplate(), dto.getParams());

        // Create message record
        MsgRecord record = new MsgRecord();
        record.setTemplateCode(dto.getTemplateCode());
        record.setReceiver(dto.getReceiver());
        record.setReceiverType(dto.getReceiverType());
        record.setChannel(dto.getChannel());
        record.setContent(content);
        record.setSendStatus(0); // pending
        record.setRetryCount(0);
        recordMapper.insert(record);

        // Send via appropriate channel
        try {
            switch (dto.getChannel()) {
                case "email":
                    sendEmail(dto.getReceiver(), subject, content);
                    break;
                case "sms":
                    sendSms(dto.getReceiver(), content);
                    break;
                case "wechat_work":
                    sendWechatWork(dto.getReceiver(), content);
                    break;
                case "dingtalk":
                    sendDingTalk(dto.getReceiver(), content);
                    break;
                default:
                    throw new BizException("不支持的发送渠道: " + dto.getChannel());
            }

            // Update record as success
            record.setSendStatus(1);
            record.setSendTime(LocalDateTime.now());
            recordMapper.updateById(record);
            log.info("Sent {} message to {} successfully", dto.getChannel(), dto.getReceiver());

        } catch (Exception e) {
            // Update record as failed
            record.setSendStatus(2);
            record.setErrorMsg(e.getMessage());
            recordMapper.updateById(record);
            log.error("Failed to send {} message to {}", dto.getChannel(), dto.getReceiver(), e);
        }
    }

    @Override
    public void retryFailedMessages() {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("send_status", 2) // failed
                .lt("retry_count", MAX_RETRY_COUNT)
                .orderBy("create_time", true)
                .limit(100); // batch size

        List<MsgRecord> failedRecords = recordMapper.selectList(wrapper);

        for (MsgRecord record : failedRecords) {
            try {
                switch (record.getChannel()) {
                    case "email":
                        sendEmail(record.getReceiver(), "重试通知", record.getContent());
                        break;
                    case "sms":
                        sendSms(record.getReceiver(), record.getContent());
                        break;
                    case "wechat_work":
                        sendWechatWork(record.getReceiver(), record.getContent());
                        break;
                    case "dingtalk":
                        sendDingTalk(record.getReceiver(), record.getContent());
                        break;
                    default:
                        log.warn("Unknown channel for retry: {}", record.getChannel());
                        continue;
                }

                record.setSendStatus(1);
                record.setSendTime(LocalDateTime.now());
                record.setRetryCount(record.getRetryCount() + 1);
                record.setErrorMsg(null);
                recordMapper.updateById(record);
                log.info("Retried message {} successfully", record.getId());

            } catch (Exception e) {
                record.setRetryCount(record.getRetryCount() + 1);
                record.setErrorMsg(e.getMessage());
                recordMapper.updateById(record);
                log.error("Retry failed for message {}", record.getId(), e);
            }
        }
    }

    @Override
    public void sendEmail(String receiver, String subject, String content) {
        NotificationConfig.EmailConfig emailConfig = notificationConfig.getEmail();
        if (!emailConfig.isEnabled()) {
            log.warn("Email channel is disabled, skipping send to {}", receiver);
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(emailConfig.getFrom());
            helper.setTo(receiver);
            helper.setSubject(subject);
            helper.setText(content, true); // true = HTML
            javaMailSender.send(message);
            log.info("Email sent to {}", receiver);
        } catch (Exception e) {
            throw new BizException("邮件发送失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendSms(String receiver, String content) {
        // SMS provider HTTP call - placeholder implementation
        // In production, integrate with actual SMS provider (e.g., Alibaba Cloud SMS, Tencent Cloud SMS)
        log.info("Sending SMS to {} with content: {}", receiver, content);

        try {
            // Example: HTTP POST to SMS provider API
            // The actual URL and parameters depend on the SMS provider
            String smsProviderUrl = "https://sms-provider.example.com/send";
            String response = webClientBuilder.build()
                    .post()
                    .uri(smsProviderUrl)
                    .bodyValue(Map.of(
                            "phone", receiver,
                            "content", content
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("SMS sent to {}, response: {}", receiver, response);
        } catch (Exception e) {
            throw new BizException("短信发送失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendWechatWork(String webhookUrl, String content) {
        NotificationConfig.WechatWorkConfig wechatConfig = notificationConfig.getWechatWork();
        if (!wechatConfig.isEnabled()) {
            log.warn("WeChat Work channel is disabled, skipping send");
            return;
        }

        String url = webhookUrl != null ? webhookUrl : wechatConfig.getWebhookUrl();
        AssertUtil.notBlank(url, "企业微信Webhook地址不能为空");

        try {
            String response = webClientBuilder.build()
                    .post()
                    .uri(url)
                    .bodyValue(Map.of(
                            "msgtype", "text",
                            "text", Map.of("content", content)
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("WeChat Work message sent, response: {}", response);
        } catch (Exception e) {
            throw new BizException("企业微信消息发送失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendDingTalk(String webhookUrl, String content) {
        NotificationConfig.DingTalkConfig dingtalkConfig = notificationConfig.getDingTalk();
        if (!dingtalkConfig.isEnabled()) {
            log.warn("DingTalk channel is disabled, skipping send");
            return;
        }

        String url = webhookUrl != null ? webhookUrl : dingtalkConfig.getWebhookUrl();
        AssertUtil.notBlank(url, "钉钉Webhook地址不能为空");

        try {
            String response = webClientBuilder.build()
                    .post()
                    .uri(url)
                    .bodyValue(Map.of(
                            "msgtype", "text",
                            "text", Map.of("content", content)
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("DingTalk message sent, response: {}", response);
        } catch (Exception e) {
            throw new BizException("钉钉消息发送失败: " + e.getMessage(), e);
        }
    }

    /**
     * Render template by replacing placeholders with actual values
     *
     * @param template template string with {key} placeholders
     * @param params   parameter map
     * @return rendered string
     */
    private String renderTemplate(String template, Map<String, String> params) {
        if (template == null || params == null) {
            return template;
        }

        String result = template;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}
