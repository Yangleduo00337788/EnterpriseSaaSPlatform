package com.flowcloud.approval.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AttachmentVO {
    private Long id;
    private String bizType;
    private Long bizId;
    private String bizName;
    private String bizCode;
    private String fieldName;
    private String originalName;
    private String fileUrl;
    private String storageType;
    private Long fileSize;
    private String mimeType;
    private String uploaderName;
    private LocalDateTime createTime;
}
