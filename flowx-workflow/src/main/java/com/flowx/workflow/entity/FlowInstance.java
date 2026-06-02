package com.flowx.workflow.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
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
@Table("flow_instance")
public class FlowInstance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Flow definition ID
     */
    @Column("definition_id")
    private Long definitionId;

    /**
     * Business key (关联业务ID)
     */
    @Column("business_key")
    private String businessKey;

    /**
     * Business type
     */
    @Column("business_type")
    private String businessType;

    /**
     * Instance title
     */
    @Column("title")
    private String title;

    /**
     * Initiator user ID
     */
    @Column("initiator_id")
    private Long initiatorId;

    /**
     * Flowable process instance ID
     */
    @Column("process_instance_id")
    private String processInstanceId;

    /**
     * Start time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column("start_time")
    private LocalDateTime startTime;

    /**
     * End time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column("end_time")
    private LocalDateTime endTime;

    /**
     * Status (0=running, 1=completed, 2=terminated, 3=cancelled)
     */
    @Column("status")
    private Integer status;

    /**
     * Process variables (JSON)
     */
    @Column("variables")
    private String variables;
}
