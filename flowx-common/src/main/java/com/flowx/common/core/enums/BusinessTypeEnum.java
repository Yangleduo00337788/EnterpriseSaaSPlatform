package com.flowx.common.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Business type enumeration for audit logging
 *
 * @author FlowX
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum BusinessTypeEnum {

    /**
     * Other operation
     */
    OTHER(0, "其他"),

    /**
     * Insert operation
     */
    INSERT(1, "新增"),

    /**
     * Update operation
     */
    UPDATE(2, "修改"),

    /**
     * Delete operation
     */
    DELETE(3, "删除"),

    /**
     * Export operation
     */
    EXPORT(4, "导出"),

    /**
     * Import operation
     */
    IMPORT(5, "导入"),

    /**
     * Login operation
     */
    LOGIN(6, "登录"),

    /**
     * Logout operation
     */
    LOGOUT(7, "登出"),

    /**
     * Approve operation
     */
    APPROVE(8, "审批");

    /**
     * Business type code
     */
    @EnumValue
    private final int code;

    /**
     * Business type description
     */
    private final String description;
}
