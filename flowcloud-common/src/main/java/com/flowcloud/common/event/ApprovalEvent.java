package com.flowcloud.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ApprovalEvent extends ApplicationEvent {

    public enum Type {
        /** 新任务分配给审批人 */
        TASK_ASSIGNED,
        /** 审批通过 */
        APPROVED,
        /** 审批驳回 */
        REJECTED,
        /** 申请已撤销 */
        CANCELLED,
        /** 催办提醒 */
        REMIND,
    }

    private final Type eventType;
    private final Long tenantId;
    /** 接收消息的目标用户 */
    private final Long targetUserId;
    private final String instanceTitle;
    private final Long instanceId;
    private final String category;
    /** 审批人/操作人名字（用于消息正文描述） */
    private final String operatorName;
    private final String comment;

    public ApprovalEvent(Object source, Type eventType, Long tenantId, Long targetUserId,
                         String instanceTitle, Long instanceId, String category,
                         String operatorName, String comment) {
        super(source);
        this.eventType = eventType;
        this.tenantId = tenantId;
        this.targetUserId = targetUserId;
        this.instanceTitle = instanceTitle;
        this.instanceId = instanceId;
        this.category = category;
        this.operatorName = operatorName;
        this.comment = comment;
    }
}