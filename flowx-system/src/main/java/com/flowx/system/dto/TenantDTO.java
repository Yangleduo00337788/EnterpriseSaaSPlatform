package com.flowx.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Tenant create/update DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class TenantDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Tenant name
     */
    @NotBlank(message = "租户名称不能为空")
    @Size(max = 100, message = "租户名称长度不能超过100个字符")
    private String tenantName;

    /**
     * Contact person name
     */
    @NotBlank(message = "联系人不能为空")
    @Size(max = 50, message = "联系人名称长度不能超过50个字符")
    private String contactName;

    /**
     * Contact phone number
     */
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String contactPhone;

    /**
     * Contact email
     */
    @Size(max = 100, message = "联系邮箱长度不能超过100个字符")
    private String contactEmail;

    /**
     * Tenant domain
     */
    @Size(max = 200, message = "域名长度不能超过200个字符")
    private String domain;

    /**
     * Tenant logo URL
     */
    @Size(max = 500, message = "Logo地址长度不能超过500个字符")
    private String logoUrl;

    /**
     * Status (0=normal, 1=expired, 2=disabled)
     */
    private Integer status;

    /**
     * Expiration time
     */
    private LocalDateTime expireTime;

    /**
     * Account limit
     */
    private Integer accountLimit;

    /**
     * Tenant package ID
     */
    private Long packageId;

    /**
     * Remark
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
