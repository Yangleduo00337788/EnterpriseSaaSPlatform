package com.flowx.system.dto;

import com.flowx.common.core.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Tenant query DTO for paginated search
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantQueryDTO extends PageQuery {

    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * Tenant name (fuzzy match)
     */
    private String tenantName;

    /**
     * Contact name (fuzzy match)
     */
    private String contactName;

    /**
     * Status filter
     */
    private Integer status;
}
