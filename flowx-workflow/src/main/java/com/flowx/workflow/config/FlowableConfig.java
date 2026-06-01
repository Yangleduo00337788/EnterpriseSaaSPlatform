package com.flowx.workflow.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.flowable.common.engine.impl.cfg.IdGenerator;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flowable engine configuration
 * <p>
 * Configures the Flowable process engine with:
 * - Disabled auto-deployment
 * - Async executor enabled
 * - Custom Snowflake ID generator
 * </p>
 *
 * @author FlowX
 * @since 1.0.0
 */
@Configuration
public class FlowableConfig {

    /**
     * Snowflake ID generator instance
     */
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    /**
     * Configure the Flowable process engine
     *
     * @return engine configuration configurer
     */
    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> engineConfigurer() {
        return configuration -> {
            // Disable Flowable's auto-deployment
            configuration.setDatabaseSchemaUpdate("true");

            // Enable async executor
            configuration.setAsyncExecutorActivate(true);

            // Set custom ID generator using Snowflake
            configuration.setIdGenerator(new IdGenerator() {
                @Override
                public String getNextId() {
                    return String.valueOf(SNOWFLAKE.nextId());
                }
            });

            // Set activity font for Chinese support
            configuration.setActivityFontName("宋体");
            configuration.setLabelFontName("宋体");
            configuration.setAnnotationFontName("宋体");
        };
    }
}
