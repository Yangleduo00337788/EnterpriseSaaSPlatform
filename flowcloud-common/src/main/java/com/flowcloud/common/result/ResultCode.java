package com.flowcloud.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    BAD_REQUEST(400, "请求参数错误"),
    TENANT_NOT_FOUND(1001, "租户不存在"),
    USER_NOT_FOUND(1002, "用户不存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    USER_DISABLED(1004, "用户已禁用"),
    TOKEN_EXPIRED(1005, "Token已过期"),
    DUPLICATE_USERNAME(1006, "用户名已存在"),
    API_CRYPTO_KEY_EXPIRED(4601, "接口加密密钥已失效");

    private final int code;
    private final String message;
}
