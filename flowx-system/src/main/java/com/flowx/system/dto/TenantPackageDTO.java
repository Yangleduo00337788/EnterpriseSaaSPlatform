package com.flowx.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * Tenant package create/update DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class TenantPackageDTO implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * Package name
     */
    @NotBlank(message = "套餐名称不能为空")
    @Size(max = 100, message = "套餐名称长度不能超过100个字符")
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
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
