package com.flowx.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 / Knife4j configuration
 *
 * @author FlowX
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * API documentation title
     */
    private static final String API_TITLE = "FlowX API";

    /**
     * API documentation description
     */
    private static final String API_DESCRIPTION = "FlowX Enterprise SaaS Platform API Documentation";

    /**
     * API version
     */
    private static final String API_VERSION = "1.0.0";

    /**
     * Security scheme name
     */
    private static final String SECURITY_SCHEME_NAME = "Bearer";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(API_TITLE)
                        .description(API_DESCRIPTION)
                        .version(API_VERSION)
                        .contact(new Contact()
                                .name("FlowX Team")
                                .email("admin@flowx.com")
                                .url("https://flowx.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .schemaRequirement(SECURITY_SCHEME_NAME, createSecurityScheme())
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }

    /**
     * Create JWT Bearer security scheme
     */
    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter JWT token (without 'Bearer ' prefix)");
    }
}
