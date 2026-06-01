package com.flowx.common.annotation;

import java.lang.annotation.*;

/**
 * Rate limit annotation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * Max request count within time window
     *
     * @return max count
     */
    int count() default 100;

    /**
     * Time window in seconds
     *
     * @return time window seconds
     */
    int time() default 60;

    /**
     * Limit type: IP or DEFAULT
     *
     * @return limit type
     */
    String limitType() default "IP";
}
