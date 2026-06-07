package com.flowcloud.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.flowcloud")
@EnableScheduling
@EnableAsync
@MapperScan({"com.flowcloud.system.mapper", "com.flowcloud.approval.mapper", "com.flowcloud.notification.mapper"})
public class FlowCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowCloudApplication.class, args);
    }

}
