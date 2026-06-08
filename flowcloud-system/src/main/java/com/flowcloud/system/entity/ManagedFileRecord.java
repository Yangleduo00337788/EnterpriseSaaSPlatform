package com.flowcloud.system.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table("attachment_file")
public class ManagedFileRecord {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long tenantId;
    private String bizType;
    private Long bizId;
    private String fieldName;
    private String originalName;
    private String fileKey;
    private String fileUrl;
    private String storageType;
    private Long fileSize;
    private String mimeType;
    private Long uploaderId;
    private String uploaderName;
    private LocalDateTime createTime;
}
