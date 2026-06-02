package com.flowx.approval.dto;

import com.flowx.common.core.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Approval query DTO with pagination
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApprovalQueryDTO extends PageQuery {

    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * Approval type ID filter
     */
    private Long typeId;

    /**
     * Initiator user ID filter
     */
    private Long initiatorId;

    /**
     * Status filter (0=draft, 1=pending, 2=approved, 3=rejected, 4=withdrawn, 5=cancelled)
     */
    private Integer status;

    /**
     * Title filter (fuzzy match)
     */
    private String title;
}
