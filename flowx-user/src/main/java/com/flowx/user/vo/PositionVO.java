package com.flowx.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Position view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class PositionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Position ID
     */
    private Long id;

    /**
     * Position name
     */
    private String positionName;

    /**
     * Position code (unique identifier)
     */
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
    private String remark;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
