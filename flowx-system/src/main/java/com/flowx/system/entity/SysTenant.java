package com.flowx.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
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
@Table("sys_tenant")
public class SysTenant extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Tenant name
     */
    @Column("tenant_name")
    private String tenantName;

    /**
     * Contact person name
     */
    @Column("contact_name")
    private String contactName;

    /**
     * Contact phone number
     */
    @Column("contact_phone")
    private String contactPhone;

    /**
     * Contact email
     */
    @Column("contact_email")
    private String contactEmail;

    /**
     * Tenant domain
     */
    @Column("domain")
    private String domain;

    /**
     * Tenant logo URL
     */
    @Column("logo_url")
    private String logoUrl;

    /**
     * Status (0=normal, 1=expired, 2=disabled)
     */
    @Column("status")
    private Integer status;

    /**
     * Expiration time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column("expire_time")
    private LocalDateTime expireTime;

    /**
     * Account limit
     */
    @Column("account_limit")
    private Integer accountLimit;

    /**
     * Tenant package ID
     */
    @Column("package_id")
    private Long packageId;

    /**
     * Remark
     */
    @Column("remark")
    private String remark;
}