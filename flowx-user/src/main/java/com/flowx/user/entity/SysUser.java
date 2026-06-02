package com.flowx.user.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * System user entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_user")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Username
     */
    @Column("username")
    private String username;

    /**
     * Password (encrypted)
     */
    @Column("password")
    private String password;

    /**
     * Nickname
     */
    @Column("nickname")
    private String nickname;

    /**
     * Email
     */
    @Column("email")
    private String email;

    /**
     * Phone
     */
    @Column("phone")
    private String phone;

    /**
     * Gender (0=unknown, 1=male, 2=female)
     */
    @Column("gender")
    private Integer gender;

    /**
     * Avatar URL
     */
    @Column("avatar")
    private String avatar;

    /**
     * Department ID
     */
    @Column("dept_id")
    private Long deptId;

    /**
     * Position ID
     */
    @Column("position_id")
    private Long positionId;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @Column("status")
    private Integer status;

    /**
     * Last login IP
     */
    @Column("login_ip")
    private String loginIp;

    /**
     * Last login time
     */
    @Column("login_time")
    private LocalDateTime loginTime;

    /**
     * Remark
     */
    @Column("remark")
    private String remark;
}
