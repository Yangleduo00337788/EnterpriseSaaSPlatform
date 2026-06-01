package com.flowx.approval.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Approval instance view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class ApprovalInstanceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Instance ID
     */
    private Long id;

    /**
     * Approval type ID
     */
    private Long typeId;

    /**
     * Type name
     */
    private String typeName;

    /**
     * Approval title
     */
    private String title;

    /**
     * Business ID
     */
    private String businessId;

    /**
     * Initiator user ID
     */
    private Long initiatorId;

    /**
     * Initiator name
     */
    private String initiatorName;

    /**
     * Flow instance ID
     */
    private Long flowInstanceId;

    /**
     * Flowable process instance ID
     */
    private String processInstanceId;

    /**
     * Form data
     */
    private Map<String, Object> formData;

    /**
     * Status (0=draft, 1=pending, 2=approved, 3=rejected, 4=withdrawn, 5=cancelled)
     */
    private Integer status;

    /**
     * Urgency level (0=normal, 1=urgent, 2=very urgent)
     */
    private Integer urgencyLevel;

    /**
     * Submit time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;

    /**
     * Complete time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeTime;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
