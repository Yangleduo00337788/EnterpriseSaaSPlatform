package com.flowx.common.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Approval status enumeration
 *
 * @author FlowX
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ApprovalStatusEnum {

    /**
     * Draft
     */
    DRAFT(0, "草稿"),

    /**
     * Pending approval
     */
    PENDING(1, "待审批"),

    /**
     * Approved
     */
    APPROVED(2, "已通过"),

    /**
     * Rejected
     */
    REJECTED(3, "已驳回"),

    /**
     * Withdrawn
     */
    WITHDRAWN(4, "已撤回"),

    /**
     * Cancelled
     */
    CANCELLED(5, "已取消");

    /**
     * Status code
     */
    @EnumValue
    private final int code;

    /**
     * Status description
     */
    private final String description;
}
