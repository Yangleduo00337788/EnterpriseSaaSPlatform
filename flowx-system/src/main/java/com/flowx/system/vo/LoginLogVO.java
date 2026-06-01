package com.flowx.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Login log view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class LoginLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Log ID
     */
    private Long id;

    /**
     * Username
     */
    private String username;

    /**
     * Login IP address
     */
    private String loginIp;

    /**
     * Login location
     */
    private String loginLocation;

    /**
     * Browser type
     */
    private String browser;

    /**
     * Operating system
     */
    private String os;

    /**
     * Login status (0=fail, 1=success)
     */
    private Integer status;

    /**
     * Message
     */
    private String msg;

    /**
     * Login time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;
}
