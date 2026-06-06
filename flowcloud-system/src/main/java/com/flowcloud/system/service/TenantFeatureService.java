package com.flowcloud.system.service;

import java.util.List;

public interface TenantFeatureService {

    List<String> getEnabledFeatures(Long tenantId);

    boolean isEnabled(String featureKey);

    void requireFeature(String featureKey);

    String resolveRequiredFeature(String requestPath);
}
