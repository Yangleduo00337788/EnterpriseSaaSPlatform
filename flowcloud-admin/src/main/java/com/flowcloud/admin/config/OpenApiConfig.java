package com.flowcloud.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("审流云 API")
                        .description("审流云企业审批平台接口文档")
                        .version("1.0.0")
                        .contact(new Contact().name("FlowCloud").email("support@flowcloud.com")));
    }
}
