package com.flowx.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Menu name
     */
    @TableField("menu_name")
    private String menuName;

    /**
     * Parent menu ID (0 for root)
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * Sort order
     */
    @TableField("sort")
    private Integer sort;

    /**
     * Route path
     */
    @TableField("path")
    private String path;

    /**
     * Component path
     */
    @TableField("component")
    private String component;

    /**
     * Menu type (0=directory, 1=menu, 2=button)
     */
    @TableField("menu_type")
    private Integer menuType;

    /**
     * Permission identifier
     */
    @TableField("permission")
    private String permission;

    /**
     * Menu icon
     */
    @TableField("icon")
    private String icon;

    /**
     * Visible (0=hidden, 1=visible)
     */
    @TableField("visible")
    private Integer visible;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @TableField("status")
    private Integer status;

    /**
     * Remark
     */
    @TableField("remark")
    private String remark;
}
