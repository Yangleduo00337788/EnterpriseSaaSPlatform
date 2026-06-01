package com.flowx.common.annotation;

import java.lang.annotation.*;

/**
 * Data scope annotation for data permission filtering
 *
 * @author FlowX
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * Department table alias
     *
     * @return dept alias
     */
    String deptAlias() default "";

    /**
     * User table alias
     *
     * @return user alias
     */
    String userAlias() default "";
}
