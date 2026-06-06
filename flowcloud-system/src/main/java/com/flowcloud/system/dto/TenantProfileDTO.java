package com.flowcloud.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TenantProfileDTO {

    @NotBlank(message = "企业名称不能为空")
    private String tenantName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String logo;
    private String themeColor;
    private String planType;
    private Integer maxUsers;
    private String expireTime;
    private String packageConfig;
    private String featureConfig;
}