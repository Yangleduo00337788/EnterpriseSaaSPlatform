package com.flowx.common.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Tenant status enumeration
 *
 * @author FlowX
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum TenantStatusEnum {

    /**
     * Normal status
     */
    NORMAL(0, "正常"),

    /**
     * Expired status
     */
    EXPIRED(1, "已过期"),

    /**
     * Disabled status
     */
    DISABLED(2, "已禁用");

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
