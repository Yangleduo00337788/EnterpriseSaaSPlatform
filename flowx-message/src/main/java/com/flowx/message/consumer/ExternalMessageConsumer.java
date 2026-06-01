package com.flowx.message.consumer;

import com.flowx.message.dto.SendExternalDTO;
import com.flowx.message.service.MsgRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for external messages (email, SMS, WeChat Work, DingTalk)
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalMessageConsumer {

    private final MsgRecordService recordService;

    /**
     * Consume email events from Kafka topic
     *
     * @param record         consumer record
     * @param acknowledgment acknowledgment
     */
    @KafkaListener(
            topics = "${flowx.kafka.topics.email:flowx-email}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onEmail(ConsumerRecord<String, SendExternalDTO> record, Acknowledgment acknowledgment) {
        processExternalMessage(record, "email", acknowledgment);
    }

    /**
     * Consume SMS events from Kafka topic
     *
     * @param record         consumer record
     * @param acknowledgment acknowledgment
     */
    @KafkaListener(
            topics = "${flowx.kafka.topics.sms:flowx-sms}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onSms(ConsumerRecord<String, SendExternalDTO> record, Acknowledgment acknowledgment) {
        processExternalMessage(record, "sms", acknowledgment);
    }

    /**
     * Consume WeChat Work events from Kafka topic
     *
     * @param record         consumer record
     * @param acknowledgment acknowledgment
     */
    @KafkaListener(
            topics = "${flowx.kafka.topics.wechat-work:flowx-wechat-work}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onWechatWork(ConsumerRecord<String, SendExternalDTO> record, Acknowledgment acknowledgment) {
        processExternalMessage(record, "wechat_work", acknowledgment);
    }

    /**
     * Consume DingTalk events from Kafka topic
     *
     * @param record         consumer record
     * @param acknowledgment acknowledgment
     */
    @KafkaListener(
            topics = "${flowx.kafka.topics.dingtalk:flowx-dingtalk}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onDingTalk(ConsumerRecord<String, SendExternalDTO> record, Acknowledgment acknowledgment) {
        processExternalMessage(record, "dingtalk", acknowledgment);
    }

    /**
     * Process external message from Kafka record
     *
     * @param record         consumer record
     * @param channel        message channel
     * @param acknowledgment acknowledgment
     */
    private void processExternalMessage(ConsumerRecord<String, SendExternalDTO> record,
                                         String channel, Acknowledgment acknowledgment) {
        try {
            SendExternalDTO dto = record.value();
            log.info("Received {} message event for receiver: {}", channel, dto.getReceiver());

            // Ensure channel matches the topic
            dto.setChannel(channel);
            recordService.sendExternalMessage(dto);

            acknowledgment.acknowledge();
            log.info("Processed {} message event for receiver: {}", channel, dto.getReceiver());

        } catch (Exception e) {
            log.error("Failed to process {} message event", channel, e);
            // In production, send to dead letter topic
            acknowledgment.acknowledge(); // Acknowledge to avoid infinite retry
        }
    }
}
