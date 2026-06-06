package com.flowcloud.common.config;

import com.flowcloud.common.context.TenantContext;
import com.mybatisflex.core.tenant.TenantFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisFlexConfig {

    @Bean
    public TenantFactory tenantFactory() {
        return () -> {
            Long tenantId = TenantContext.getTenantId();
            return tenantId != null ? new Object[]{tenantId} : null;
        };
    }
}
