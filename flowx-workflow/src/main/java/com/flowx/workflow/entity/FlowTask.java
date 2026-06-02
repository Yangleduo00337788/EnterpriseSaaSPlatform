package com.flowx.workflow.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Flow task entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("flow_task")
public class FlowTask extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Flow instance ID
     */
    @Column("instance_id")
    private Long instanceId;

    /**
     * Flowable task ID
     */
    @Column("task_id")
    private String taskId;

    /**
     * Task name
     */
    @Column("task_name")
    private String taskName;

    /**
     * Task key (definition key of the task)
     */
    @Column("task_key")
    private String taskKey;

    /**
     * Assignee user ID
     */
    @Column("assignee_id")
    private Long assigneeId;

    /**
     * Candidate users (comma-separated user IDs)
     */
    @Column("candidate_users")
    private String candidateUsers;

    /**
     * Candidate groups (comma-separated group codes)
     */
    @Column("candidate_groups")
    private String candidateGroups;

    /**
     * Claim time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column("claim_time")
    private LocalDateTime claimTime;

    /**
     * Complete time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column("complete_time")
    private LocalDateTime completeTime;

    /**
     * Status (0=pending, 1=claimed, 2=completed, 3=delegated)
     */
    @Column("status")
    private Integer status;

    /**
     * Task comment
     */
    @Column("comment")
    private String comment;
}
