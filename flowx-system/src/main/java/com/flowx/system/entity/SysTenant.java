package com.flowx.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * System tenant entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant")
public class SysTenant extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Tenant name
     */
    @TableField("tenant_name")
    private String tenantName;

    /**
     * Contact person name
     */
    @TableField("contact_name")
    private String contactName;

    /**
     * Contact phone number
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * Contact email
     */
    @TableField("contact_email")
    private String contactEmail;

    /**
     * Tenant domain
     */
    @TableField("domain")
    private String domain;

    /**
     * Tenant logo URL
     */
    @TableField("logo_url")
    private String logoUrl;

    /**
     * Status (0=normal, 1=expired, 2=disabled)
     */
    @TableField("status")
    private Integer status;

    /**
     * Expiration time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * Account limit
     */
    @TableField("account_limit")
    private Integer accountLimit;

    /**
     * Tenant package ID
     */
    @TableField("package_id")
    private Long packageId;

    /**
     * Remark
     */
    @TableField("remark")
    private String remark;
}
