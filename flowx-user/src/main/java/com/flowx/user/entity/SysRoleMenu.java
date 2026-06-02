package com.flowx.user.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * Role-menu association entity (join table)
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@Table("sys_role_menu")
public class SysRoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary ID
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private Long id;

    /**
     * Role ID
     */
    @Column("role_id")
    private Long roleId;

    /**
     * Menu ID
     */
    @Column("menu_id")
    private Long menuId;
}
