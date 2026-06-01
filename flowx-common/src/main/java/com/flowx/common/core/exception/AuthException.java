package com.flowx.common.core.exception;

import com.flowx.common.core.result.ResultCodeEnum;

/**
 * Authentication exception (401)
 *
 * @author FlowX
 * @since 1.0.0
 */
public class AuthException extends BizException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with default 401 code
     */
    public AuthException() {
        super(ResultCodeEnum.UNAUTHORIZED.getCode(), ResultCodeEnum.UNAUTHORIZED.getMessage());
    }

    /**
     * Constructor with custom message
     *
     * @param message exception message
     */
    public AuthException(String message) {
        super(ResultCodeEnum.UNAUTHORIZED.getCode(), message);
    }

    /**
     * Constructor with custom code and message
     *
     * @param code    error code
     * @param message exception message
     */
    public AuthException(int code, String message) {
        super(code, message);
    }

    /**
     * Constructor with result code enum
     *
     * @param resultCodeEnum result code enum
     */
    public AuthException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum);
    }
}
