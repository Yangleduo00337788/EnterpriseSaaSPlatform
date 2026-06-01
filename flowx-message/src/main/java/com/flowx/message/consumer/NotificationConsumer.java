package com.flowx.message.consumer;

import com.flowx.message.dto.SendNotificationDTO;
import com.flowx.message.service.MsgNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for in-app notifications
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final MsgNotificationService notificationService;

    /**
     * Consume notification events from Kafka topic
     *
     * @param record         consumer record
     * @param acknowledgment acknowledgment
     */
    @KafkaListener(
            topics = "${flowx.kafka.topics.notification:flowx-notification}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onNotification(ConsumerRecord<String, SendNotificationDTO> record, Acknowledgment acknowledgment) {
        try {
            SendNotificationDTO dto = record.value();
            log.info("Received notification event for user: {}, title: {}", dto.getUserId(), dto.getTitle());

            notificationService.sendNotification(dto);

            acknowledgment.acknowledge();
            log.info("Processed notification event for user: {}", dto.getUserId());

        } catch (Exception e) {
            log.error("Failed to process notification event from topic: flowx-notification", e);
            // In production, send to dead letter topic
            acknowledgment.acknowledge(); // Acknowledge to avoid infinite retry
        }
    }
}
