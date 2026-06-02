package com.flowx.common.core.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Gender enumeration
 *
 * @author FlowX
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum GenderEnum {

    /**
     * Unknown gender
     */
    UNKNOWN(0, "未知"),

    /**
     * Male
     */
    MALE(1, "男"),

    /**
     * Female
     */
    FEMALE(2, "女");

    /**
     * Gender code
     */
    @EnumValue
    private final int code;

    /**
     * Gender description
     */
    private final String description;
}