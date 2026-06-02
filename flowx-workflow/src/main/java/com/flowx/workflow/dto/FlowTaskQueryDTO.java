package com.flowx.workflow.dto;

import com.flowx.common.core.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Flow task query DTO with pagination
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowTaskQueryDTO extends PageQuery {

    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * Assignee user ID filter
     */
    private Long assigneeId;

    /**
     * Flow instance ID filter
     */
    private Long instanceId;

    /**
     * Status filter (0=pending, 1=claimed, 2=completed, 3=delegated)
     */
    private Integer status;
}
