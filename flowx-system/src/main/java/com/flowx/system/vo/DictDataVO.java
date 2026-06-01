package com.flowx.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Dictionary data view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class DictDataVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Dict data ID
     */
    private Long id;

    /**
     * Dictionary type
     */
    private String dictType;

    /**
     * Dictionary label
     */
    private String dictLabel;

    /**
     * Dictionary value
     */
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
    private String remark;

    /**
     * CSS class
     */
    private String cssClass;

    /**
     * List class (tag type for front-end)
     */
    private String listClass;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
