package com.flowx.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * Flow category create/update DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class FlowCategoryDTO implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;

    /**
     * Category name
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    private String categoryName;

    /**
     * Category code (unique)
     */
    @NotBlank(message = "分类编码不能为空")
    @Size(max = 100, message = "分类编码长度不能超过100个字符")
    private String categoryCode;

    /**
     * Sort order
     */
    private Integer sort;

    /**
     * Icon
     */
    @Size(max = 200, message = "图标长度不能超过200个字符")
    private String icon;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;
}
