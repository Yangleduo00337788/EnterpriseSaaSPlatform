package com.flowcloud.admin.interceptor;

import com.flowcloud.system.service.TenantFeatureService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class FeatureGateInterceptor implements HandlerInterceptor {

    private final TenantFeatureService tenantFeatureService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String feature = tenantFeatureService.resolveRequiredFeature(request.getRequestURI());
        if (feature != null) {
            tenantFeatureService.requireFeature(feature);
        }
        return true;
    }
}
