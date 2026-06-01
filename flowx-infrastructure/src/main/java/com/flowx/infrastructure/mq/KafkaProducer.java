package com.flowx.infrastructure.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka producer service wrapping KafkaTemplate.
 * Provides convenient methods for sending messages.
 */
@Slf4j
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Send message to topic.
     *
     * @param topic   the topic name
     * @param message the message object
     */
    public void send(String topic, Object message) {
        log.info("Sending message to topic: {}", topic);
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, message);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send message to topic: {}", topic, ex);
            } else {
                log.info("Message sent to topic: {}, partition: {}, offset: {}",
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    /**
     * Send message to topic with key.
     *
     * @param topic   the topic name
     * @param key     the message key
     * @param message the message object
     */
    public void send(String topic, String key, Object message) {
        log.info("Sending message to topic: {}, key: {}", topic, key);
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, message);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send message to topic: {}, key: {}", topic, key, ex);
            } else {
                log.info("Message sent to topic: {}, key: {}, partition: {}, offset: {}",
                        topic, key,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    /**
     * Send message to topic with callback.
     *
     * @param topic    the topic name
     * @param message  the message object
     * @param callback the callback to execute after send completes
     */
    public void sendWithCallback(String topic, Object message,
                                 java.util.function.BiConsumer<SendResult<String, Object>, Throwable> callback) {
        log.info("Sending message to topic with callback: {}", topic);
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, message);
        future.whenComplete(callback);
    }

    /**
     * Send message to topic synchronously.
     *
     * @param topic   the topic name
     * @param message the message object
     * @return the send result
     * @throws Exception if send fails
     */
    public SendResult<String, Object> sendSync(String topic, Object message) throws Exception {
        log.info("Sending message synchronously to topic: {}", topic);
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, message);
        return future.get();
    }
}
