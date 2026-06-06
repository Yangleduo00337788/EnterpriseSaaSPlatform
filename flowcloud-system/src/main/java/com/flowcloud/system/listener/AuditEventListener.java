package com.flowcloud.system.listener;

import com.flowcloud.common.event.AuditEvent;
import com.flowcloud.system.entity.SysAuditLog;
import com.flowcloud.system.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventListener {

    private final AuditLogService auditLogService;

    @Async
    @EventListener
    public void onAuditEvent(AuditEvent event) {
        try {
            SysAuditLog log = new SysAuditLog();
            log.setTenantId(event.getTenantId());
            log.setUserId(event.getUserId());
            log.setUserName(event.getUserName());
            log.setAction(event.getAction());
            log.setTargetType(event.getTargetType());
            log.setTargetId(event.getTargetId());
            log.setTargetName(event.getTargetName());
            log.setResult(event.getResult() != null ? event.getResult() : "success");
            log.setDetail(event.getDetail());
            log.setIp(event.getIp());
            log.setCreateTime(LocalDateTime.now());
            auditLogService.record(log);
        } catch (Exception e) {
            log.error("审计日志写入失败: action={}", event.getAction(), e);
        }
    }
}