package com.flowcloud.system.vo;

import lombok.Data;

import java.util.List;

@Data
public class TenantProfileVO {

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
    private Integer currentUsers;
    private Integer remainingUserSlots;
    private String expireTime;
    private Boolean expired;
    private String packageConfig;
    private String featureConfig;
    private List<String> enabledFeatures;
}
