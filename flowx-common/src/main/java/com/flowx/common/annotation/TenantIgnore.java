package com.flowx.common.annotation;

import java.lang.annotation.*;

/**
 * Tenant ignore annotation - marker annotation to skip tenant filtering
 *
 * @author FlowX
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TenantIgnore {
}
