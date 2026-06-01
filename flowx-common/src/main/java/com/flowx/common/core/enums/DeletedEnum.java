package com.flowx.common.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Deleted flag enumeration
 *
 * @author FlowX
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DeletedEnum {

    /**
     * Normal (not deleted)
     */
    NORMAL(0, "正常"),

    /**
     * Deleted
     */
    DELETED(1, "已删除");

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
