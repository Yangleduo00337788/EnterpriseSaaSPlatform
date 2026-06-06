package com.flowcloud.system.service.impl;

import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.system.entity.SysTenant;
import com.flowcloud.system.mapper.SysTenantMapper;
import com.flowcloud.system.service.TenantFeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TenantFeatureServiceImpl implements TenantFeatureService {

    private static final List<String> FEATURE_KEYS = List.of("approval", "report", "message", "tenantSettings");
    private static final Map<String, List<String>> PATH_FEATURES = Map.of(
            "approval", List.of("/api/approval/**"),
            "report", List.of("/api/report/**"),
            "message", List.of("/api/messages/**"),
            "tenantSettings", List.of("/api/system/tenant/**")
    );

    private final SysTenantMapper tenantMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public List<String> getEnabledFeatures(Long tenantId) {
        SysTenant tenant = tenantMapper.selectOneById(tenantId);
        if (tenant == null || !StringUtils.hasText(tenant.getFeatureConfig())) {
            return FEATURE_KEYS;
        }
        return FEATURE_KEYS.stream()
                .filter(key -> cn.hutool.json.JSONUtil.parseObj(tenant.getFeatureConfig()).getBool(key, true))
                .toList();
    }

    @Override
    public boolean isEnabled(String featureKey) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return true;
        }
        return getEnabledFeatures(tenantId).contains(featureKey);
    }

    @Override
    public void requireFeature(String featureKey) {
        if (!isEnabled(featureKey)) {
            throw new BusinessException("当前租户未开通功能：" + featureKey);
        }
    }

    @Override
    public String resolveRequiredFeature(String requestPath) {
        for (Map.Entry<String, List<String>> entry : PATH_FEATURES.entrySet()) {
            for (String pattern : entry.getValue()) {
                if (pathMatcher.match(pattern, requestPath)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
}
