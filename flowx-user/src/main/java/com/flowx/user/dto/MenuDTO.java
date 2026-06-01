package com.flowx.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * Menu create/update DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class MenuDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Menu name
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    /**
     * Parent menu ID (0 for root)
     */
    @NotNull(message = "父菜单ID不能为空")
    private Long parentId;

    /**
     * Sort order
     */
    private Integer sort;

    /**
     * Route path
     */
    private String path;

    /**
     * Component path
     */
    private String component;

    /**
     * Menu type (0=directory, 1=menu, 2=button)
     */
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    /**
     * Permission identifier
     */
    private String permission;

    /**
     * Menu icon
     */
    private String icon;

    /**
     * Visible (0=hidden, 1=visible)
     */
    private Integer visible;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;

    /**
     * Remark
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
