package com.flowx.user.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * System menu entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_menu")
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Menu name
     */
    @Column("menu_name")
    private String menuName;

    /**
     * Parent menu ID (0 for root)
     */
    @Column("parent_id")
    private Long parentId;

    /**
     * Sort order
     */
    @Column("sort")
    private Integer sort;

    /**
     * Route path
     */
    @Column("path")
    private String path;

    /**
     * Component path
     */
    @Column("component")
    private String component;

    /**
     * Menu type (0=directory, 1=menu, 2=button)
     */
    @Column("menu_type")
    private Integer menuType;

    /**
     * Permission identifier
     */
    @Column("permission")
    private String permission;

    /**
     * Menu icon
     */
    @Column("icon")
    private String icon;

    /**
     * Visible (0=hidden, 1=visible)
     */
    @Column("visible")
    private Integer visible;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @Column("status")
    private Integer status;

    /**
     * Remark
     */
    @Column("remark")
    private String remark;
}
