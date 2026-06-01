package com.flowx.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("flow_task")
public class FlowTask extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Flow instance ID
     */
    @TableField("instance_id")
    private Long instanceId;

    /**
     * Flowable task ID
     */
    @TableField("task_id")
    private String taskId;

    /**
     * Task name
     */
    @TableField("task_name")
    private String taskName;

    /**
     * Task key (definition key of the task)
     */
    @TableField("task_key")
    private String taskKey;

    /**
     * Assignee user ID
     */
    @TableField("assignee_id")
    private Long assigneeId;

    /**
     * Candidate users (comma-separated user IDs)
     */
    @TableField("candidate_users")
    private String candidateUsers;

    /**
     * Candidate groups (comma-separated group codes)
     */
    @TableField("candidate_groups")
    private String candidateGroups;

    /**
     * Claim time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("claim_time")
    private LocalDateTime claimTime;

    /**
     * Complete time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("complete_time")
    private LocalDateTime completeTime;

    /**
     * Status (0=pending, 1=claimed, 2=completed, 3=delegated)
     */
    @TableField("status")
    private Integer status;

    /**
     * Task comment
     */
    @TableField("comment")
    private String comment;
}
