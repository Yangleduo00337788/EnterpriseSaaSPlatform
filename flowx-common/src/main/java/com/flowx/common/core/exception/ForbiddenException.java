package com.flowx.common.core.exception;

import com.flowx.common.core.result.ResultCodeEnum;

/**
 * Forbidden exception (403)
 *
 * @author FlowX
 * @since 1.0.0
 */
public class ForbiddenException extends BizException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with default 403 code
     */
    public ForbiddenException() {
        super(ResultCodeEnum.FORBIDDEN.getCode(), ResultCodeEnum.FORBIDDEN.getMessage());
    }

    /**
     * Constructor with custom message
     *
     * @param message exception message
     */
    public ForbiddenException(String message) {
        super(ResultCodeEnum.FORBIDDEN.getCode(), message);
    }

    /**
     * Constructor with custom code and message
     *
     * @param code    error code
     * @param message exception message
     */
    public ForbiddenException(int code, String message) {
        super(code, message);
    }

    /**
     * Constructor with result code enum
     *
     * @param resultCodeEnum result code enum
     */
    public ForbiddenException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum);
    }
}
