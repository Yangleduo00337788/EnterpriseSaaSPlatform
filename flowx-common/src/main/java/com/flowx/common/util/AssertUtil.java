package com.flowx.common.util;

import com.flowx.common.core.exception.BizException;

/**
 * Assertion utility class
 *
 * @author FlowX
 * @since 1.0.0
 */
public final class AssertUtil {

    private AssertUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Assert object is not null
     *
     * @param obj     object to check
     * @param message error message
     * @throws BizException if object is null
     */
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new BizException(message);
        }
    }

    /**
     * Assert object is not null with error code
     *
     * @param obj     object to check
     * @param code    error code
     * @param message error message
     * @throws BizException if object is null
     */
    public static void notNull(Object obj, int code, String message) {
        if (obj == null) {
            throw new BizException(code, message);
        }
    }

    /**
     * Assert string is not blank
     *
     * @param str     string to check
     * @param message error message
     * @throws BizException if string is null or blank
     */
    public static void notBlank(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new BizException(message);
        }
    }

    /**
     * Assert string is not blank with error code
     *
     * @param str     string to check
     * @param code    error code
     * @param message error message
     * @throws BizException if string is null or blank
     */
    public static void notBlank(String str, int code, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new BizException(code, message);
        }
    }

    /**
     * Assert condition is true
     *
     * @param condition condition to check
     * @param message   error message
     * @throws BizException if condition is false
     */
    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new BizException(message);
        }
    }

    /**
     * Assert condition is true with error code
     *
     * @param condition condition to check
     * @param code      error code
     * @param message   error message
     * @throws BizException if condition is false
     */
    public static void isTrue(boolean condition, int code, String message) {
        if (!condition) {
            throw new BizException(code, message);
        }
    }

    /**
     * Assert condition is false
     *
     * @param condition condition to check
     * @param message   error message
     * @throws BizException if condition is true
     */
    public static void isFalse(boolean condition, String message) {
        if (condition) {
            throw new BizException(message);
        }
    }

    /**
     * Assert string does not exceed max length
     *
     * @param str       string to check
     * @param maxLength max length
     * @param message   error message
     * @throws BizException if string exceeds max length
     */
    public static void maxLength(String str, int maxLength, String message) {
        if (str != null && str.length() > maxLength) {
            throw new BizException(message);
        }
    }

    /**
     * Assert number is positive
     *
     * @param number  number to check
     * @param message error message
     * @throws BizException if number is not positive
     */
    public static void positive(Number number, String message) {
        if (number == null || number.longValue() <= 0) {
            throw new BizException(message);
        }
    }
}
