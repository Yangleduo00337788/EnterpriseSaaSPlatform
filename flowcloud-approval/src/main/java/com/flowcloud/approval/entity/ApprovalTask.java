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
@Table("approval_task")
public class ApprovalTask extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column(tenantId = true)
    private Long tenantId;

    private Long instanceId;
    private String instanceNo;
    private String title;
    private Integer nodeIndex;
    private String nodeName;
    private Long approverId;
    private String approverName;
    private String status;
    private String comment;
    private LocalDateTime handleTime;
}
