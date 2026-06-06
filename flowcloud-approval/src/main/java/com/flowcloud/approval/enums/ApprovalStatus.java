package com.flowcloud.approval.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprovalStatus {

    DRAFT("draft", "草稿"),
    PENDING("pending", "审批中"),
    APPROVED("approved", "已通过"),
    REJECTED("rejected", "已驳回"),
    CANCELLED("cancelled", "已撤销");

    private final String code;
    private final String label;
}
