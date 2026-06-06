package com.flowcloud.notification.entity;

import com.flowcloud.common.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_message_template")
public class SysMessageTemplate extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column(tenantId = true)
    private Long tenantId;

    private String templateCode;
    private String templateName;
    private String eventType;
    private String titleTemplate;
    private String contentTemplate;
    private Integer status;
}
