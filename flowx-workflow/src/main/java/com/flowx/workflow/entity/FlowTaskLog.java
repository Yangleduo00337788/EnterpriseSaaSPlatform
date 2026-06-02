package com.flowx.workflow.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Flow task operation log entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("flow_task_log")
public class FlowTaskLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Flow instance ID
     */
    @Column("instance_id")
    private Long instanceId;

    /**
     * Flow task ID
     */
    @Column("task_id")
    private Long taskId;

    /**
     * Flowable task ID
     */
    @Column("flowable_task_id")
    private String flowableTaskId;

    /**
     * Operator user ID
     */
    @Column("operator_id")
    private Long operatorId;

    /**
     * Operation type (claim, complete, delegate, reject, withdraw)
     */
    @Column("operation_type")
    private String operationType;

    /**
     * Comment
     */
    @Column("comment")
    private String comment;

    /**
     * Operation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column("operate_time")
    private LocalDateTime operateTime;
}
