package com.flowx.message.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Notification channel configuration
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "flowx.notification")
public class NotificationConfig {

    /**
     * Email configuration
     */
    private EmailConfig email = new EmailConfig();

    /**
     * WeChat Work configuration
     */
    private WechatWorkConfig wechatWork = new WechatWorkConfig();

    /**
     * DingTalk configuration
     */
    private DingTalkConfig dingTalk = new DingTalkConfig();

    /**
     * Email channel configuration
     */
    @Data
    public static class EmailConfig {
        /**
         * Whether email is enabled
         */
        private boolean enabled = false;

        /**
         * SMTP host
         */
        private String host;

        /**
         * SMTP port
         */
        private int port = 587;

        /**
         * SMTP username
         */
        private String username;

        /**
         * SMTP password
         */
        private String password;

        /**
         * Sender email address
         */
        private String from;
    }

    /**
     * WeChat Work channel configuration
     */
    @Data
    public static class WechatWorkConfig {
        /**
         * Whether WeChat Work is enabled
         */
        private boolean enabled = false;

        /**
         * Webhook URL for group messages
         */
        private String webhookUrl;

        /**
         * Application agent ID
         */
        private String agentId;

        /**
         * Corporation ID
         */
        private String corpId;

        /**
         * Corporation secret
         */
        private String corpSecret;
    }

    /**
     * DingTalk channel configuration
     */
    @Data
    public static class DingTalkConfig {
        /**
         * Whether DingTalk is enabled
         */
        private boolean enabled = false;

        /**
         * Webhook URL for robot messages
         */
        private String webhookUrl;

        /**
         * Application key
         */
        private String appKey;

        /**
         * Application secret
         */
        private String appSecret;
    }
}
