package com.flowx.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Role create/update DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class RoleDTO implements Serializable {

    /**
     * Primary key ID
     */
    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * Role name
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    /**
     * Role key (unique identifier)
     */
    @NotBlank(message = "角色标识不能为空")
    @Size(max = 50, message = "角色标识长度不能超过50个字符")
    private String roleKey;

    /**
     * Sort order
     */
    private Integer sort;

    /**
     * Data scope (1=all, 2=custom, 3=dept, 4=dept and below, 5=self)
     */
    private Integer dataScope;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;

    /**
     * Remark
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    /**
     * Assigned menu IDs
     */
    private List<Long> menuIds;
}
