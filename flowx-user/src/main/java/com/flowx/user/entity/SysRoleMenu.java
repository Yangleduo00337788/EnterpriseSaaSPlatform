package com.flowx.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * Role-menu association entity (join table)
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Role ID
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * Menu ID
     */
    @TableField("menu_id")
    private Long menuId;
}
