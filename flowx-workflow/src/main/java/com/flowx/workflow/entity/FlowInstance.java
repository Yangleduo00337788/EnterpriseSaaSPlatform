package com.flowx.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Flow instance entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("flow_instance")
public class FlowInstance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Flow definition ID
     */
    @TableField("definition_id")
    private Long definitionId;

    /**
     * Business key (关联业务ID)
     */
    @TableField("business_key")
    private String businessKey;

    /**
     * Business type
     */
    @TableField("business_type")
    private String businessType;

    /**
     * Instance title
     */
    @TableField("title")
    private String title;

    /**
     * Initiator user ID
     */
    @TableField("initiator_id")
    private Long initiatorId;

    /**
     * Flowable process instance ID
     */
    @TableField("process_instance_id")
    private String processInstanceId;

    /**
     * Start time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * End time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * Status (0=running, 1=completed, 2=terminated, 3=cancelled)
     */
    @TableField("status")
    private Integer status;

    /**
     * Process variables (JSON)
     */
    @TableField("variables")
    private String variables;
}
