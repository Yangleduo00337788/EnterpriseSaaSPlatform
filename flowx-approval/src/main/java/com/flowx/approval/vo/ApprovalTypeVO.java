package com.flowx.approval.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Approval type view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class ApprovalTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Type ID
     */
    private Long id;

    /**
     * Type name
     */
    private String typeName;

    /**
     * Type code
     */
    private String typeCode;

    /**
     * Icon
     */
    private String icon;

    /**
     * Associated flow key
     */
    private String flowKey;

    /**
     * Sort order
     */
    private Integer sort;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;

    /**
     * Form schema (JSON)
     */
    private String formSchema;

    /**
     * Description
     */
    private String description;

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
