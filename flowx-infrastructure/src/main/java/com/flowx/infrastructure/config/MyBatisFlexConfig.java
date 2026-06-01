package com.flowx.infrastructure.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.flowx.infrastructure.persistence.handler.AutoFillHandler;
import com.flowx.infrastructure.persistence.handler.TenantLineHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Flex configuration.
 * Registers tenant interceptor and auto-fill handler.
 */
@Configuration
public class MyBatisFlexConfig {

    private final AutoFillHandler autoFillHandler;

    public MyBatisFlexConfig(AutoFillHandler autoFillHandler) {
        this.autoFillHandler = autoFillHandler;
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // Add tenant line interceptor
        TenantLineInnerInterceptor tenantInterceptor =
                new TenantLineInnerInterceptor(new TenantLineHandlerImpl());
        interceptor.addInnerInterceptor(tenantInterceptor);

        return interceptor;
    }

    @Bean
    public AutoFillHandler metaObjectHandler() {
        return autoFillHandler;
    }
}
