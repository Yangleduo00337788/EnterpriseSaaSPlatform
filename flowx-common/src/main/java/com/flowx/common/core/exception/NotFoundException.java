package com.flowx.common.core.exception;

import com.flowx.common.core.result.ResultCodeEnum;

/**
 * Resource not found exception
 *
 * @author FlowX
 * @since 1.0.0
 */
public class NotFoundException extends BizException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with default 404 code
     */
    public NotFoundException() {
        super(ResultCodeEnum.NOT_FOUND.getCode(), ResultCodeEnum.NOT_FOUND.getMessage());
    }

    /**
     * Constructor with custom message
     *
     * @param message exception message
     */
    public NotFoundException(String message) {
        super(ResultCodeEnum.NOT_FOUND.getCode(), message);
    }

    /**
     * Constructor with custom code and message
     *
     * @param code    error code
     * @param message exception message
     */
    public NotFoundException(int code, String message) {
        super(code, message);
    }

    /**
     * Constructor with result code enum
     *
     * @param resultCodeEnum result code enum
     */
    public NotFoundException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum);
    }
}
