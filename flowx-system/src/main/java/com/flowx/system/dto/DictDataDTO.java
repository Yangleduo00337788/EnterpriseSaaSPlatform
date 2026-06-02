package com.flowx.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * Dictionary data create/update DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class DictDataDTO implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * Dictionary type
     */
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

    /**
     * Dictionary label
     */
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String dictLabel;

    /**
     * Dictionary value
     */
    @NotBlank(message = "字典值不能为空")
    @Size(max = 100, message = "字典值长度不能超过100个字符")
    private String dictValue;

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

    /**
     * CSS class
     */
    @Size(max = 100, message = "CSS类名长度不能超过100个字符")
    private String cssClass;

    /**
     * List class (tag type for front-end)
     */
    @Size(max = 100, message = "列表类名长度不能超过100个字符")
    private String listClass;
}
