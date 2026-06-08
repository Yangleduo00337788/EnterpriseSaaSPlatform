package com.flowcloud.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StorageSettingsDTO {

    @NotBlank(message = "存储方式不能为空")
    private String storageType;
    private String localPath;
    private String localBaseUrl;
    private String minioEndpoint;
    private String minioAccessKey;
    private String minioSecretKey;
    private String minioBucket;
    private String minioBaseUrl;
    private String minioConsoleUrl;
}
