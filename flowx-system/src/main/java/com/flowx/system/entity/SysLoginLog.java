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
 * Login log entity (no tenant isolation)
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@Table("sys_login_log")
public class SysLoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary ID
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private Long id;

    /**
     * Username
     */
    @Column("username")
    private String username;

    /**
     * Login IP address
     */
    @Column("login_ip")
    private String loginIp;

    /**
     * Login location
     */
    @Column("login_location")
    private String loginLocation;

    /**
     * Browser type
     */
    @Column("browser")
    private String browser;

    /**
     * Operating system
     */
    @Column("os")
    private String os;

    /**
     * Login status (0=fail, 1=success)
     */
    @Column("status")
    private Integer status;

    /**
     * Message (success or failure reason)
     */
    @Column("msg")
    private String msg;

    /**
     * Login time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column("login_time")
    private LocalDateTime loginTime;
}
