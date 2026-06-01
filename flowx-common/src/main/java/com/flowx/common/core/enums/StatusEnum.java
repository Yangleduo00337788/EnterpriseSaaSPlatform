package com.flowx.common.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Status enumeration
 *
 * @author FlowX
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {

    /**
     * Enable status
     */
    ENABLE(0, "正常"),

    /**
     * Disable status
     */
    DISABLE(1, "禁用");

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
