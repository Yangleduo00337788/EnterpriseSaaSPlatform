package com.flowx.workflow.dto;

import com.flowx.common.core.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Flow instance query DTO with pagination
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowInstanceQueryDTO extends PageQuery {

    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * Flow definition ID filter
     */
    private Long definitionId;

    /**
     * Initiator user ID filter
     */
    private Long initiatorId;

    /**
     * Status filter (0=running, 1=completed, 2=terminated, 3=cancelled)
     */
    private Integer status;

    /**
     * Title filter (fuzzy match)
     */
    private String title;
}
