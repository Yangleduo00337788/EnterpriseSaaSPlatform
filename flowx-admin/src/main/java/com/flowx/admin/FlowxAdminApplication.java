package com.flowx.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * FlowX Admin application entry point
 *
 * @author FlowX
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.flowx")
@MapperScan("com.flowx.**.mapper")
@EnableScheduling
@EnableAsync
public class FlowxAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowxAdminApplication.class, args);
    }
}
