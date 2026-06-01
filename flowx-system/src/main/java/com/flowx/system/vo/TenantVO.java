package com.flowx.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Tenant view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class TenantVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Tenant ID
     */
    private Long id;

    /**
     * Tenant name
     */
    private String tenantName;

    /**
     * Contact person name
     */
    private String contactName;

    /**
     * Contact phone number
     */
    private String contactPhone;

    /**
     * Contact email
     */
    private String contactEmail;

    /**
     * Tenant domain
     */
    private String domain;

    /**
     * Tenant logo URL
     */
    private String logoUrl;

    /**
     * Status (0=normal, 1=expired, 2=disabled)
     */
    private Integer status;

    /**
     * Expiration time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
     * Package name
     */
    private String packageName;

    /**
     * Remark
     */
    private String remark;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
