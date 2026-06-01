package com.flowx.common.core.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Result code enumeration
 *
 * @author FlowX
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ResultCodeEnum {

    /**
     * Success
     */
    SUCCESS(200, "操作成功"),

    /**
     * Bad request
     */
    BAD_REQUEST(400, "请求参数错误"),

    /**
     * Unauthorized
     */
    UNAUTHORIZED(401, "未认证或认证已过期"),

    /**
     * Forbidden
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * Not found
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * Internal server error
     */
    INTERNAL_ERROR(500, "服务器内部错误"),

    /**
     * User not found
     */
    USER_NOT_FOUND(1001, "用户不存在"),

    /**
     * Role not found
     */
    ROLE_NOT_FOUND(1002, "角色不存在"),

    /**
     * Tenant not found
     */
    TENANT_NOT_FOUND(1003, "租户不存在"),

    /**
     * Approval not found
     */
    APPROVAL_NOT_FOUND(1004, "审批记录不存在"),

    /**
     * File upload failed
     */
    FILE_UPLOAD_FAILED(1005, "文件上传失败"),

    /**
     * AI service error
     */
    AI_SERVICE_ERROR(1006, "AI服务调用失败"),

    /**
     * Duplicate username
     */
    DUPLICATE_USERNAME(1007, "用户名已存在"),

    /**
     * Invalid password
     */
    INVALID_PASSWORD(1008, "密码错误"),

    /**
     * Account disabled
     */
    ACCOUNT_DISABLED(1009, "账号已被禁用"),

    /**
     * Token expired
     */
    TOKEN_EXPIRED(1010, "Token已过期"),

    /**
     * Invalid token
     */
    INVALID_TOKEN(1011, "无效的Token"),

    /**
     * Duplicate data
     */
    DUPLICATE_DATA(1012, "数据已存在"),

    /**
     * Operation failed
     */
    OPERATION_FAILED(1013, "操作失败"),

    /**
     * Rate limit exceeded
     */
    RATE_LIMIT_EXCEEDED(1014, "请求频率超限"),

    /**
     * Tenant expired
     */
    TENANT_EXPIRED(1015, "租户已过期"),

    /**
     * Tenant disabled
     */
    TENANT_DISABLED(1016, "租户已被禁用"),

    /**
     * Department not found
     */
    DEPT_NOT_FOUND(1017, "部门不存在"),

    /**
     * Post not found
     */
    POST_NOT_FOUND(1018, "岗位不存在"),

    /**
     * Dictionary not found
     */
    DICT_NOT_FOUND(1019, "字典不存在"),

    /**
     * Menu not found
     */
    MENU_NOT_FOUND(1020, "菜单不存在"),

    /**
     * Workflow definition not found
     */
    WORKFLOW_DEF_NOT_FOUND(1021, "流程定义不存在"),

    /**
     * Workflow instance not found
     */
    WORKFLOW_INSTANCE_NOT_FOUND(1022, "流程实例不存在"),

    /**
     * Duplicate email
     */
    DUPLICATE_EMAIL(1023, "邮箱已存在"),

    /**
     * Duplicate phone
     */
    DUPLICATE_PHONE(1024, "手机号已存在"),

    /**
     * File size exceeded
     */
    FILE_SIZE_EXCEEDED(1025, "文件大小超限"),

    /**
     * File type not allowed
     */
    FILE_TYPE_NOT_ALLOWED(1026, "文件类型不允许");

    /**
     * Result code
     */
    private final int code;

    /**
     * Result message
     */
    private final String message;
}
