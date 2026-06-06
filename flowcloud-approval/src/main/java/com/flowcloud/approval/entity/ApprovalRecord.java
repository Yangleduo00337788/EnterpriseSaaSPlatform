package com.flowcloud.approval.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table("approval_record")
public class ApprovalRecord {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column(tenantId = true)
    private Long tenantId;

    private Long instanceId;
    private Integer nodeIndex;
    private String nodeName;
    private Long operatorId;
    private String operatorName;
    private String action;
    private String comment;
    private LocalDateTime createTime;
}
