package com.flowx.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * User view object with full details
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * User ID
     */
    private Long id;

    /**
     * Username
     */
    private String username;

    /**
     * Nickname
     */
    private String nickname;

    /**
     * Email
     */
    private String email;

    /**
     * Phone
     */
    private String phone;

    /**
     * Gender (0=unknown, 1=male, 2=female)
     */
    private Integer gender;

    /**
     * Avatar URL
     */
    private String avatar;

    /**
     * Department ID
     */
    private Long deptId;

    /**
     * Department name
     */
    private String deptName;

    /**
     * Position ID
     */
    private Long positionId;

    /**
     * Position name
     */
    private String positionName;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;

    /**
     * Last login IP
     */
    private String loginIp;

    /**
     * Last login time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;

    /**
     * Remark
     */
    private String remark;

    /**
     * Assigned roles
     */
    private List<RoleVO> roles;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
