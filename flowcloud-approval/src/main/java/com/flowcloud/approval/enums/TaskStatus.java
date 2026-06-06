package com.flowcloud.approval.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatus {

    PENDING("pending", "待处理"),
    APPROVED("approved", "已通过"),
    REJECTED("rejected", "已驳回"),
    TRANSFERRED("transferred", "已转交");

    private final String code;
    private final String label;
}
