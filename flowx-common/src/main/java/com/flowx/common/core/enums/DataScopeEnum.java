package com.flowx.common.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data scope enumeration
 *
 * @author FlowX
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DataScopeEnum {

    /**
     * All data
     */
    ALL(1, "全部数据"),

    /**
     * Department data only
     */
    DEPT(2, "本部门数据"),

    /**
     * Department and child departments data
     */
    DEPT_AND_CHILD(3, "本部门及以下"),

    /**
     * Self data only
     */
    SELF(4, "仅本人"),

    /**
     * Custom data scope
     */
    CUSTOM(5, "自定义");

    /**
     * Scope code
     */
    @EnumValue
    private final int code;

    /**
     * Scope description
     */
    private final String description;
}
