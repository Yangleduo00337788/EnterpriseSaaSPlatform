package com.flowx.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("flow_task_log")
public class FlowTaskLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Flow instance ID
     */
    @TableField("instance_id")
    private Long instanceId;

    /**
     * Flow task ID
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * Flowable task ID
     */
    @TableField("flowable_task_id")
    private String flowableTaskId;

    /**
     * Operator user ID
     */
    @TableField("operator_id")
    private Long operatorId;

    /**
     * Operation type (claim, complete, delegate, reject, withdraw)
     */
    @TableField("operation_type")
    private String operationType;

    /**
     * Comment
     */
    @TableField("comment")
    private String comment;

    /**
     * Operation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("operate_time")
    private LocalDateTime operateTime;
}
