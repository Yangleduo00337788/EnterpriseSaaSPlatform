package com.flowx.common.core.result;

import lombok.Data;

import java.io.Serializable;

/**
 * Unified response wrapper
 *
 * @param <T> data type
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Response code
     */
    private int code;

    /**
     * Response message
     */
    private String message;

    /**
     * Response data
     */
    private T data;

    /**
     * Private constructor
     */
    private R() {
    }

    /**
     * Private constructor with code and message
     *
     * @param code    response code
     * @param message response message
     */
    private R(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Private constructor with code, message and data
     *
     * @param code    response code
     * @param message response message
     * @param data    response data
     */
    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * Success with default message
     *
     * @param <T> data type
     * @return success response
     */
    public static <T> R<T> ok() {
        return new R<>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
    }

    /**
     * Success with data
     *
     * @param data response data
     * @param <T>  data type
     * @return success response with data
     */
    public static <T> R<T> ok(T data) {
        return new R<>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), data);
    }

    /**
     * Success with custom message and data
     *
     * @param message custom message
     * @param data    response data
     * @param <T>     data type
     * @return success response with message and data
     */
    public static <T> R<T> ok(String message, T data) {
        return new R<>(ResultCodeEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * Failure with default message
     *
     * @param <T> data type
     * @return failure response
     */
    public static <T> R<T> fail() {
        return new R<>(ResultCodeEnum.INTERNAL_ERROR.getCode(), ResultCodeEnum.INTERNAL_ERROR.getMessage());
    }

    /**
     * Failure with custom message
     *
     * @param message custom message
     * @param <T>     data type
     * @return failure response with message
     */
    public static <T> R<T> fail(String message) {
        return new R<>(ResultCodeEnum.INTERNAL_ERROR.getCode(), message);
    }

    /**
     * Failure with custom code and message
     *
     * @param code    custom code
     * @param message custom message
     * @param <T>     data type
     * @return failure response with code and message
     */
    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message);
    }

    /**
     * Failure with result code enum
     *
     * @param resultCodeEnum result code enum
     * @param <T>            data type
     * @return failure response with enum
     */
    public static <T> R<T> fail(ResultCodeEnum resultCodeEnum) {
        return new R<>(resultCodeEnum.getCode(), resultCodeEnum.getMessage());
    }

    /**
     * Check if response is successful
     *
     * @return true if success
     */
    public boolean isSuccess() {
        return this.code == ResultCodeEnum.SUCCESS.getCode();
    }
}
