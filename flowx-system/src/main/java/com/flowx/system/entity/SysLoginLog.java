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
 * Login log entity (no tenant isolation)
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@TableName("sys_login_log")
public class SysLoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Username
     */
    @TableField("username")
    private String username;

    /**
     * Login IP address
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * Login location
     */
    @TableField("login_location")
    private String loginLocation;

    /**
     * Browser type
     */
    @TableField("browser")
    private String browser;

    /**
     * Operating system
     */
    @TableField("os")
    private String os;

    /**
     * Login status (0=fail, 1=success)
     */
    @TableField("status")
    private Integer status;

    /**
     * Message (success or failure reason)
     */
    @TableField("msg")
    private String msg;

    /**
     * Login time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("login_time")
    private LocalDateTime loginTime;
}
