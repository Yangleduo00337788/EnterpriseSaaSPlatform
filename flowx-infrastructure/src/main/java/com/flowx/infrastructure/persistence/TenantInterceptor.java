package com.flowx.infrastructure.persistence;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Tenant interceptor that extracts tenant_id from request header
 * and sets it to TenantContext (ThreadLocal).
 */
@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-Tenant-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantIdStr = request.getHeader(TENANT_HEADER);
        if (StringUtils.hasText(tenantIdStr)) {
            try {
                Long tenantId = Long.parseLong(tenantIdStr);
                TenantContext.setTenantId(tenantId);
                log.debug("Set tenant context: {}", tenantId);
            } catch (NumberFormatException e) {
                log.warn("Invalid tenant id header: {}", tenantIdStr);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        TenantContext.clear();
        log.debug("Cleared tenant context");
    }
}
