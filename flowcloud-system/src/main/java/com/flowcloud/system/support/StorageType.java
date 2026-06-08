package com.flowcloud.system.support;

public enum StorageType {
    LOCAL,
    MINIO;

    public static StorageType fromValue(String value) {
        if (value == null || value.isBlank()) {
            return LOCAL;
        }
        try {
            return StorageType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return LOCAL;
        }
    }
}
