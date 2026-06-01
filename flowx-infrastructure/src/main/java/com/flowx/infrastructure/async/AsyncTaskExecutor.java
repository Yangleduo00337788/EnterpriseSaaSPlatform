package com.flowx.infrastructure.async;

import com.flowx.infrastructure.mq.KafkaProducer;
import com.flowx.infrastructure.mq.TopicConstant;
import com.flowx.infrastructure.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Async task executor for common background operations.
 * Uses the configured asyncTaskExecutor thread pool.
 */
@Slf4j
@Component
public class AsyncTaskExecutor {

    private final KafkaProducer kafkaProducer;
    private final RedisService redisService;

    public AsyncTaskExecutor(KafkaProducer kafkaProducer, RedisService redisService) {
        this.kafkaProducer = kafkaProducer;
        this.redisService = redisService;
    }

    /**
     * Send notification asynchronously via Kafka.
     *
     * @param notificationType the notification type (email, sms, wechat, dingtalk)
     * @param message          the notification message
     */
    @Async("asyncTaskExecutor")
    public void asyncSendNotification(String notificationType, Object message) {
        try {
            String topic = resolveNotificationTopic(notificationType);
            kafkaProducer.send(topic, message);
            log.info("Async notification sent: type={}, topic={}", notificationType, topic);
        } catch (Exception e) {
            log.error("Failed to send async notification: type={}", notificationType, e);
        }
    }

    /**
     * Save audit log asynchronously via Kafka.
     *
     * @param auditLog the audit log data
     */
    @Async("asyncTaskExecutor")
    public void asyncSaveAuditLog(Object auditLog) {
        try {
            kafkaProducer.send(TopicConstant.TOPIC_AUDIT_LOG, auditLog);
            log.info("Async audit log saved");
        } catch (Exception e) {
            log.error("Failed to save async audit log", e);
        }
    }

    /**
     * Update cache asynchronously.
     *
     * @param key     the cache key
     * @param value   the cache value
     * @param timeout the timeout value
     * @param unit    the timeout unit
     */
    @Async("asyncTaskExecutor")
    public void asyncUpdateCache(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisService.set(key, value, timeout, unit);
            log.debug("Async cache updated: key={}", key);
        } catch (Exception e) {
            log.error("Failed to update async cache: key={}", key, e);
        }
    }

    /**
     * Delete cache asynchronously.
     *
     * @param key the cache key
     */
    @Async("asyncTaskExecutor")
    public void asyncDeleteCache(String key) {
        try {
            redisService.delete(key);
            log.debug("Async cache deleted: key={}", key);
        } catch (Exception e) {
            log.error("Failed to delete async cache: key={}", key, e);
        }
    }

    /**
     * Execute custom async task.
     *
     * @param task the task to execute
     */
    @Async("asyncTaskExecutor")
    public void asyncExecute(Runnable task) {
        try {
            task.run();
            log.debug("Async task executed successfully");
        } catch (Exception e) {
            log.error("Failed to execute async task", e);
        }
    }

    /**
     * Resolve Kafka topic based on notification type.
     */
    private String resolveNotificationTopic(String notificationType) {
        return switch (notificationType.toLowerCase()) {
            case "email" -> TopicConstant.TOPIC_EMAIL;
            case "sms" -> TopicConstant.TOPIC_SMS;
            case "wechat", "wechat-work" -> TopicConstant.TOPIC_WECHAT_WORK;
            case "dingtalk" -> TopicConstant.TOPIC_DINGTALK;
            default -> TopicConstant.TOPIC_NOTIFICATION;
        };
    }
}
