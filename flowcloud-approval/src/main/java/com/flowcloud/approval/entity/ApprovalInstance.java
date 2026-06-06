package com.flowcloud.approval.entity;

import com.flowcloud.common.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("approval_instance")
public class ApprovalInstance extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column(tenantId = true)
    private Long tenantId;

    private String instanceNo;
    private Long templateId;
    private String templateName;
    private String category;
    private String title;
    private Long applicantId;
    private String applicantName;
    private Long deptId;
    private String formData;
    private String flowConfigSnapshot;
    private String status;
    private Integer currentNode;
    private String currentApprovers;
    private LocalDateTime submitTime;
    private LocalDateTime finishTime;
}
