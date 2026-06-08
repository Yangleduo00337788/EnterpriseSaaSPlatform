package com.flowcloud.system.vo;

import lombok.Data;

@Data
public class StorageSettingsVO {

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
