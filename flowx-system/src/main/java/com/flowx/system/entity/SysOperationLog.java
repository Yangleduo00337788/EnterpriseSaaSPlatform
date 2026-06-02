package com.flowx.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Operation log entity (no tenant isolation)
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@Table("sys_operation_log")
public class SysOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary ID
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private Long id;

    /**
     * Operation module title
     */
    @Column("title")
    private String title;

    /**
     * Business type (0=other, 1=insert, 2=update, 3=delete, 4=export, 5=import)
     */
    @Column("business_type")
    private Integer businessType;

    /**
     * Request method (class.method)
     */
    @Column("method")
    private String method;

    /**
     * HTTP request method (GET, POST, etc.)
     */
    @Column("request_method")
    private String requestMethod;

    /**
     * Request URL
     */
    @Column("request_url")
    private String requestUrl;

    /**
     * Request parameters
     */
    @Column("request_param")
    private String requestParam;

    /**
     * Response result
     */
    @Column("response_result")
    private String responseResult;

    /**
     * Operator type (0=other, 1=backend user, 2=app user)
     */
    @Column("operator_type")
    private Integer operatorType;

    /**
     * Operator user ID
     */
    @Column("oper_user_id")
    private Long operUserId;

    /**
     * Operator user name
     */
    @Column("oper_user_name")
    private String operUserName;

    /**
     * Operator IP address
     */
    @Column("oper_ip")
    private String operIp;

    /**
     * Operator location
     */
    @Column("oper_location")
    private String operLocation;

    /**
     * Operation status (0=fail, 1=success)
     */
    @Column("status")
    private Integer status;

    /**
     * Error message
     */
    @Column("error_msg")
    private String errorMsg;

    /**
     * Operation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column("oper_time")
    private LocalDateTime operTime;

    /**
     * Cost time (milliseconds)
     */
    @Column("cost_time")
    private Long costTime;
}
