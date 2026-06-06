package com.flowcloud.system.entity;

import com.flowcloud.common.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_tenant")
public class SysTenant extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String tenantCode;
    private String tenantName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String logo;
    private String themeColor;
    private Integer status;
    private String planType;
    private Integer maxUsers;
    private LocalDateTime expireTime;
    private String packageConfig;
    private String featureConfig;
}
