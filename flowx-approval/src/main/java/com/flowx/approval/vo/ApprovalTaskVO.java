package com.flowx.approval.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Approval task VO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class ApprovalTaskVO {

    private Long id;
    private Long instanceId;
    private String instanceTitle;
    private Long assigneeId;
    private String assigneeName;
    private String taskName;
    private String comment;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime completeTime;
}