package com.flowx.common.annotation;

import java.lang.annotation.*;

/**
 * Repeat submit prevention annotation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {

    /**
     * Interval in milliseconds, default 1000ms
     *
     * @return interval milliseconds
     */
    int interval() default 1000;

    /**
     * Error message
     *
     * @return message
     */
    String message() default "不允许重复提交，请稍后重试";
}
