package com.flowcloud.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AuditEvent extends ApplicationEvent {

    private final Long tenantId;
    private final Long userId;
    private final String userName;
    private final String action;
    private final String targetType;
    private final String targetId;
    private final String targetName;
    private final String result;
    private final String detail;
    private final String ip;

    public AuditEvent(Object source, Long tenantId, Long userId, String userName,
                      String action, String targetType, String targetId, String targetName,
                      String result, String detail, String ip) {
        super(source);
        this.tenantId = tenantId;
        this.userId = userId;
        this.userName = userName;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.targetName = targetName;
        this.result = result;
        this.detail = detail;
        this.ip = ip;
    }

    /** 便捷构造：成功动作，无 detail */
    public static AuditEvent success(Object source, Long tenantId, Long userId, String userName,
                                     String action, String targetType, String targetId, String targetName) {
        return new AuditEvent(source, tenantId, userId, userName, action,
                targetType, targetId, targetName, "success", null, null);
    }

    /**
     * 最简便捷构造：source 传 AuditEvent.class，不需要 targetType/targetId，只记录 module+action+detail。
     * userName / ip 由监听器从 UserContext 补填。
     */
    public static AuditEvent of(Long userId, Long tenantId,
                                String action, String module, String detail, String ip) {
        return new AuditEvent(AuditEvent.class, tenantId, userId, null,
                action, module, null, detail, "success", detail, ip);
    }
}