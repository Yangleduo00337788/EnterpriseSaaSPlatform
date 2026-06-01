package com.flowx.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * Position create/update DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class PositionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Position name
     */
    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 50, message = "岗位名称长度不能超过50个字符")
    private String positionName;

    /**
     * Position code (unique identifier)
     */
    @NotBlank(message = "岗位编码不能为空")
    @Size(max = 50, message = "岗位编码长度不能超过50个字符")
    private String positionCode;

    /**
     * Sort order
     */
    private Integer sort;

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
