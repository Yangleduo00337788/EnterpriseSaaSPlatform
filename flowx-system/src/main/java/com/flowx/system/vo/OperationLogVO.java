package com.flowx.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Operation log view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class OperationLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Log ID
     */
    private Long id;

    /**
     * Operation module title
     */
    private String title;

    /**
     * Business type
     */
    private Integer businessType;

    /**
     * Request method (class.method)
     */
    private String method;

    /**
     * HTTP request method
     */
    private String requestMethod;

    /**
     * Request URL
     */
    private String requestUrl;

    /**
     * Request parameters
     */
    private String requestParam;

    /**
     * Response result
     */
    private String responseResult;

    /**
     * Operator type
     */
    private Integer operatorType;

    /**
     * Operator user ID
     */
    private Long operUserId;

    /**
     * Operator user name
     */
    private String operUserName;

    /**
     * Operator IP address
     */
    private String operIp;

    /**
     * Operator location
     */
    private String operLocation;

    /**
     * Operation status (0=fail, 1=success)
     */
    private Integer status;

    /**
     * Error message
     */
    private String errorMsg;

    /**
     * Operation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operTime;

    /**
     * Cost time (milliseconds)
     */
    private Long costTime;
}
