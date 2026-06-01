package com.flowx.infrastructure.mq;

/**
 * Kafka topic constants for the FlowX platform.
 */
public final class TopicConstant {

    private TopicConstant() {
        // utility class
    }

    /** Email notification topic */
    public static final String TOPIC_EMAIL = "flowx-email";

    /** SMS notification topic */
    public static final String TOPIC_SMS = "flowx-sms";

    /** General notification topic */
    public static final String TOPIC_NOTIFICATION = "flowx-notification";

    /** WeChat Work notification topic */
    public static final String TOPIC_WECHAT_WORK = "flowx-wechat-work";

    /** DingTalk notification topic */
    public static final String TOPIC_DINGTALK = "flowx-dingtalk";

    /** Audit log topic */
    public static final String TOPIC_AUDIT_LOG = "flowx-audit-log";
}
