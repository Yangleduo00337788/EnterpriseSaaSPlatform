package com.flowx.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Username
     */
    @TableField("username")
    private String username;

    /**
     * Password (encrypted)
     */
    @TableField("password")
    private String password;

    /**
     * Nickname
     */
    @TableField("nickname")
    private String nickname;

    /**
     * Email
     */
    @TableField("email")
    private String email;

    /**
     * Phone
     */
    @TableField("phone")
    private String phone;

    /**
     * Gender (0=unknown, 1=male, 2=female)
     */
    @TableField("gender")
    private Integer gender;

    /**
     * Avatar URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * Department ID
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * Position ID
     */
    @TableField("position_id")
    private Long positionId;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @TableField("status")
    private Integer status;

    /**
     * Last login IP
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * Last login time
     */
    @TableField("login_time")
    private LocalDateTime loginTime;

    /**
     * Remark
     */
    @TableField("remark")
    private String remark;
}
