package com.flowx.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("sys_operation_log")
public class SysOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Operation module title
     */
    @TableField("title")
    private String title;

    /**
     * Business type (0=other, 1=insert, 2=update, 3=delete, 4=export, 5=import)
     */
    @TableField("business_type")
    private Integer businessType;

    /**
     * Request method (class.method)
     */
    @TableField("method")
    private String method;

    /**
     * HTTP request method (GET, POST, etc.)
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * Request URL
     */
    @TableField("request_url")
    private String requestUrl;

    /**
     * Request parameters
     */
    @TableField("request_param")
    private String requestParam;

    /**
     * Response result
     */
    @TableField("response_result")
    private String responseResult;

    /**
     * Operator type (0=other, 1=backend user, 2=app user)
     */
    @TableField("operator_type")
    private Integer operatorType;

    /**
     * Operator user ID
     */
    @TableField("oper_user_id")
    private Long operUserId;

    /**
     * Operator user name
     */
    @TableField("oper_user_name")
    private String operUserName;

    /**
     * Operator IP address
     */
    @TableField("oper_ip")
    private String operIp;

    /**
     * Operator location
     */
    @TableField("oper_location")
    private String operLocation;

    /**
     * Operation status (0=fail, 1=success)
     */
    @TableField("status")
    private Integer status;

    /**
     * Error message
     */
    @TableField("error_msg")
    private String errorMsg;

    /**
     * Operation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("oper_time")
    private LocalDateTime operTime;

    /**
     * Cost time (milliseconds)
     */
    @TableField("cost_time")
    private Long costTime;
}
