package com.flowx.workflow.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Flow task log view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class FlowTaskLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Log ID
     */
    private Long id;

    /**
     * Flow instance ID
     */
    private Long instanceId;

    /**
     * Flow task ID
     */
    private Long taskId;

    /**
     * Flowable task ID
     */
    private String flowableTaskId;

    /**
     * Operator user ID
     */
    private Long operatorId;

    /**
     * Operator name
     */
    private String operatorName;

    /**
     * Operation type (claim, complete, delegate, reject, withdraw)
     */
    private String operationType;

    /**
     * Comment
     */
    private String comment;

    /**
     * Operation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operateTime;
}
