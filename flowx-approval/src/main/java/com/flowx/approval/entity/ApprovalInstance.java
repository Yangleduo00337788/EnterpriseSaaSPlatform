package com.flowx.approval.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Approval instance entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("approval_instance")
public class ApprovalInstance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Approval type ID
     */
    @Column("type_id")
    private Long typeId;

    /**
     * Approval title
     */
    @Column("title")
    private String title;

    /**
     * Business ID (关联业务ID)
     */
    @Column("business_id")
    private String businessId;

    /**
     * Initiator user ID
     */
    @Column("initiator_id")
    private Long initiatorId;

    /**
     * Flow instance ID (关联流程实例)
     */
    @Column("flow_instance_id")
    private Long flowInstanceId;

    /**
     * Flowable process instance ID
     */
    @Column("process_instance_id")
    private String processInstanceId;

    /**
     * Form data (JSON)
     */
    @Column("form_data")
    private String formData;

    /**
     * Status (0=draft, 1=pending, 2=approved, 3=rejected, 4=withdrawn, 5=cancelled)
     */
    @Column("status")
    private Integer status;

    /**
     * Urgency level (0=normal, 1=urgent, 2=very urgent)
     */
    @Column("urgency_level")
    private Integer urgencyLevel;

    /**
     * Submit time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column("submit_time")
    private LocalDateTime submitTime;

    /**
     * Complete time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column("complete_time")
    private LocalDateTime completeTime;
}
