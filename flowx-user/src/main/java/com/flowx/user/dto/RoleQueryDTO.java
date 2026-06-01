package com.flowx.user.dto;

import com.flowx.common.core.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Role query DTO for paginated search
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQueryDTO extends PageQuery {

    private static final long serialVersionUID = 1L;

    /**
     * Role name (fuzzy match)
     */
    private String roleName;

    /**
     * Role key (fuzzy match)
     */
    private String roleKey;

    /**
     * Status filter
     */
    private Integer status;
}
