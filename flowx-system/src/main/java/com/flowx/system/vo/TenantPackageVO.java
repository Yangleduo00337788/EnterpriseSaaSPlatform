package com.flowx.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Tenant package view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class TenantPackageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Package ID
     */
    private Long id;

    /**
     * Package name
     */
    private String packageName;

    /**
     * Menu IDs (JSON array string)
     */
    private String menuIds;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;

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
