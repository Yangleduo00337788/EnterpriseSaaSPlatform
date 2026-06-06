package com.flowcloud.notification.listener;

import com.flowcloud.common.event.ApprovalEvent;
import com.flowcloud.notification.service.MessageService;
import com.flowcloud.notification.service.MessageTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalEventListener {

    private final MessageService messageService;
    private final MessageTemplateService messageTemplateService;

    @EventListener
    public void onApprovalEvent(ApprovalEvent event) {
        try {
            String title = messageTemplateService.renderTitle(event);
            String content = messageTemplateService.renderContent(event);
            messageService.sendMessage(
                    event.getTenantId(),
                    event.getTargetUserId(),
                    title,
                    content,
                    event.getCategory(),
                    event.getInstanceId()
            );
        } catch (Exception e) {
            log.error("发送审批消息失败: eventType={}, instanceId={}", event.getEventType(), event.getInstanceId(), e);
        }
    }

}