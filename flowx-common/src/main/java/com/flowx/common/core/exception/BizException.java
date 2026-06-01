package com.flowx.common.core.exception;

import com.flowx.common.core.result.ResultCodeEnum;
import lombok.Getter;

/**
 * Business exception
 *
 * @author FlowX
 * @since 1.0.0
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Error code
     */
    private final int code;

    /**
     * Constructor with message
     *
     * @param message exception message
     */
    public BizException(String message) {
        super(message);
        this.code = ResultCodeEnum.INTERNAL_ERROR.getCode();
    }

    /**
     * Constructor with code and message
     *
     * @param code    error code
     * @param message exception message
     */
    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Constructor with result code enum
     *
     * @param resultCodeEnum result code enum
     */
    public BizException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    /**
     * Constructor with cause
     *
     * @param message exception message
     * @param cause   root cause
     */
    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCodeEnum.INTERNAL_ERROR.getCode();
    }

    /**
     * Constructor with code, message and cause
     *
     * @param code    error code
     * @param message exception message
     * @param cause   root cause
     */
    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
