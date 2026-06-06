package com.flowcloud.approval.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table("approval_template_version")
public class ApprovalTemplateVersion {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long tenantId;
    private Long templateId;
    private Integer version;
    private String flowConfig;
    private String formSchema;
    private String remark;
    private LocalDateTime createTime;
}