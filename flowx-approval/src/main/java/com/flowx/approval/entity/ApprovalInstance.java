package com.flowx.approval.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("approval_instance")
public class ApprovalInstance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Approval type ID
     */
    @TableField("type_id")
    private Long typeId;

    /**
     * Approval title
     */
    @TableField("title")
    private String title;

    /**
     * Business ID (关联业务ID)
     */
    @TableField("business_id")
    private String businessId;

    /**
     * Initiator user ID
     */
    @TableField("initiator_id")
    private Long initiatorId;

    /**
     * Flow instance ID (关联流程实例)
     */
    @TableField("flow_instance_id")
    private Long flowInstanceId;

    /**
     * Flowable process instance ID
     */
    @TableField("process_instance_id")
    private String processInstanceId;

    /**
     * Form data (JSON)
     */
    @TableField("form_data")
    private String formData;

    /**
     * Status (0=draft, 1=pending, 2=approved, 3=rejected, 4=withdrawn, 5=cancelled)
     */
    @TableField("status")
    private Integer status;

    /**
     * Urgency level (0=normal, 1=urgent, 2=very urgent)
     */
    @TableField("urgency_level")
    private Integer urgencyLevel;

    /**
     * Submit time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("submit_time")
    private LocalDateTime submitTime;

    /**
     * Complete time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("complete_time")
    private LocalDateTime completeTime;
}
