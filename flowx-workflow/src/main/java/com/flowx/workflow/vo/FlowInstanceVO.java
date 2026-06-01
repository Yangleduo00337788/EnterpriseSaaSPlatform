package com.flowx.workflow.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Flow instance view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class FlowInstanceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Instance ID
     */
    private Long id;

    /**
     * Flow definition ID
     */
    private Long definitionId;

    /**
     * Definition name
     */
    private String definitionName;

    /**
     * Business key
     */
    private String businessKey;

    /**
     * Business type
     */
    private String businessType;

    /**
     * Instance title
     */
    private String title;

    /**
     * Initiator user ID
     */
    private Long initiatorId;

    /**
     * Initiator name
     */
    private String initiatorName;

    /**
     * Flowable process instance ID
     */
    private String processInstanceId;

    /**
     * Start time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * End time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * Status (0=running, 1=completed, 2=terminated, 3=cancelled)
     */
    private Integer status;

    /**
     * Process variables (JSON)
     */
    private Map<String, Object> variables;

    /**
     * Task history list
     */
    private List<FlowTaskVO> taskHistory;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
