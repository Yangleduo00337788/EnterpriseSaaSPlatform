package com.flowx.approval.dto;

import lombok.Data;

/**
 * Approval task completion DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class ApprovalTaskCompleteDTO {

    private Long id;

    /**
     * Approval comment
     */
    private String comment;
}