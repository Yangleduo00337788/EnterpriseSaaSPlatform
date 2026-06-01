package com.flowx.common.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Message type enumeration
 *
 * @author FlowX
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum MessageTypeEnum {

    /**
     * Notice message
     */
    NOTICE(1, "通知"),

    /**
     * Alert message
     */
    ALERT(2, "预警"),

    /**
     * Todo message
     */
    TODO(3, "待办");

    /**
     * Type code
     */
    @EnumValue
    private final int code;

    /**
     * Type description
     */
    private final String description;
}
