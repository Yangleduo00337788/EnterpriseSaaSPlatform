package com.flowx.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Menu view object with tree structure
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class MenuVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Menu ID
     */
    private Long id;

    /**
     * Menu name
     */
    private String menuName;

    /**
     * Parent menu ID (0 for root)
     */
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
    private String remark;

    /**
     * Child menus
     */
    private List<MenuVO> children;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
