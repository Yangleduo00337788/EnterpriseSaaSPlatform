package com.flowx.common.core.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Menu type enumeration
 *
 * @author FlowX
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum MenuTypeEnum {

    /**
     * Directory
     */
    DIR(1, "目录"),

    /**
     * Menu
     */
    MENU(2, "菜单"),

    /**
     * Button
     */
    BUTTON(3, "按钮");

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