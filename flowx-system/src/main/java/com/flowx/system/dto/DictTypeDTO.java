package com.flowx.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * Dictionary type create/update DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class DictTypeDTO implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * Dictionary name
     */
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称长度不能超过100个字符")
    private String dictName;

    /**
     * Dictionary type (unique)
     */
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

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
