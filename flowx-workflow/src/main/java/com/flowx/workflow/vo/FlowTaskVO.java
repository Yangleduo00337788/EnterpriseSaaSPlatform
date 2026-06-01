package com.flowx.workflow.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Flow task view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class FlowTaskVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Task ID
     */
    private Long id;

    /**
     * Flow instance ID
     */
    private Long instanceId;

    /**
     * Instance title
     */
    private String instanceTitle;

    /**
     * Flowable task ID
     */
    private String taskId;

    /**
     * Task name
     */
    private String taskName;

    /**
     * Task key
     */
    private String taskKey;

    /**
     * Assignee user ID
     */
    private Long assigneeId;

    /**
     * Assignee name
     */
    private String assigneeName;

    /**
     * Candidate users (comma-separated user IDs)
     */
    private String candidateUsers;

    /**
     * Candidate groups (comma-separated group codes)
     */
    private String candidateGroups;

    /**
     * Claim time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime claimTime;

    /**
     * Complete time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeTime;

    /**
     * Status (0=pending, 1=claimed, 2=completed, 3=delegated)
     */
    private Integer status;

    /**
     * Task comment
     */
    private String comment;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
