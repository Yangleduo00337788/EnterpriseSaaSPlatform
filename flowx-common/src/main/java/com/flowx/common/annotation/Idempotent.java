package com.flowx.common.annotation;

import java.lang.annotation.*;

/**
 * Idempotent annotation - ensures operation is idempotent
 *
 * @author FlowX
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * Idempotent time window in seconds
     *
     * @return time seconds
     */
    int time() default 5;

    /**
     * Error message when idempotent check fails
     *
     * @return message
     */
    String message() default "请勿重复操作";
}
