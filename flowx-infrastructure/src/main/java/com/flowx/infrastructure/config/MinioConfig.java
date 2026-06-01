package com.flowx.infrastructure.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO object storage configuration.
 * Reads properties with prefix "minio" from application.yml.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /**
     * MinIO server endpoint URL
     */
    private String endpoint;

    /**
     * MinIO access key
     */
    private String accessKey;

    /**
     * MinIO secret key
     */
    private String secretKey;

    /**
     * Default bucket name
     */
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
