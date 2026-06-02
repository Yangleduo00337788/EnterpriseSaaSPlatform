package com.flowx.infrastructure.config;

import com.mybatisflex.core.tenant.TenantFactory;
import com.mybatisflex.core.tenant.TenantManager;
import com.flowx.infrastructure.persistence.TenantContext;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * MyBatis-Flex configuration.
 * Registers tenant factory for multi-tenant data isolation.
 * Tables without tenant_id column should be queried within
 * TenantManager.withoutTenantCondition() block.
 */
@Configuration
public class MyBatisFlexConfig {

    @PostConstruct
    public void initTenantManager() {
        TenantManager.setTenantFactory(new TenantFactory() {
            @Override
            @SuppressWarnings("deprecation")
            public Object[] getTenantIds() {
                return getTenantIds(null);
            }

            @Override
            public Object[] getTenantIds(String tableName) {
                Long tenantId = TenantContext.getTenantId();
                return new Object[]{tenantId != null ? tenantId : 1L};
            }
        });
    }
}