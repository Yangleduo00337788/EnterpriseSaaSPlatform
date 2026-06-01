package com.flowx.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * Department create/update DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class DeptDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Department name
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称长度不能超过50个字符")
    private String deptName;

    /**
     * Parent department ID (0 for root)
     */
    @NotNull(message = "父部门ID不能为空")
    private Long parentId;

    /**
     * Sort order
     */
    private Integer sort;

    /**
     * Department leader
     */
    @Size(max = 30, message = "负责人名称长度不能超过30个字符")
    private String leader;

    /**
     * Contact phone
     */
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String phone;

    /**
     * Contact email
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;

    /**
     * Display order number
     */
    private Integer orderNum;
}
