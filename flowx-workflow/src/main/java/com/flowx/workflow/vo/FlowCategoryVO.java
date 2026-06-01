package com.flowx.workflow.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Flow category view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class FlowCategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Category ID
     */
    private Long id;

    /**
     * Category name
     */
    private String categoryName;

    /**
     * Category code
     */
    private String categoryCode;

    /**
     * Sort order
     */
    private Integer sort;

    /**
     * Icon
     */
    private String icon;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * Update time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
