package com.flowcloud.approval.entity;

import com.flowcloud.common.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("approval_template")
public class ApprovalTemplate extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column(tenantId = true)
    private Long tenantId;

    private String templateCode;
    private String templateName;
    private String category;
    private String description;
    private String formSchema;
    private String flowConfig;
    private Integer status;   // 0=草稿 1=已发布 2=已停用
    private Integer sort;
    private Integer pubVersion;
}
