package com.flowx.user.dto;

import com.flowx.common.core.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User query DTO for paginated search
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageQuery {

    private static final long serialVersionUID = 1L;

    /**
     * Username (fuzzy match)
     */
    private String username;

    /**
     * Nickname (fuzzy match)
     */
    private String nickname;

    /**
     * Phone (fuzzy match)
     */
    private String phone;

    /**
     * Email (fuzzy match)
     */
    private String email;

    /**
     * Status filter
     */
    private Integer status;

    /**
     * Department ID filter
     */
    private Long deptId;

    /**
     * Role ID filter
     */
    private Long roleId;
}
