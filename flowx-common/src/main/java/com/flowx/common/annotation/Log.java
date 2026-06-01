package com.flowx.common.annotation;

import com.flowx.common.core.enums.BusinessTypeEnum;

import java.lang.annotation.*;

/**
 * Operation log annotation for audit logging
 *
 * @author FlowX
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * Operation title/module name
     *
     * @return title
     */
    String title() default "";

    /**
     * Business type
     *
     * @return business type enum
     */
    BusinessTypeEnum businessType() default BusinessTypeEnum.OTHER;

    /**
     * Whether to save request data
     *
     * @return true to save request data
     */
    boolean isSaveRequestData() default true;

    /**
     * Whether to save response data
     *
     * @return true to save response data
     */
    boolean isSaveResponseData() default true;
}
