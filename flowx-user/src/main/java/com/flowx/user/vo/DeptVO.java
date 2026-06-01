package com.flowx.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Department view object with tree structure
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class DeptVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Department ID
     */
    private Long id;

    /**
     * Department name
     */
    private String deptName;

    /**
     * Parent department ID (0 for root)
     */
    private Long parentId;

    /**
     * Sort order
     */
    private Integer sort;

    /**
     * Department leader
     */
    private String leader;

    /**
     * Contact phone
     */
    private String phone;

    /**
     * Contact email
     */
    private String email;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;

    /**
     * Display order number
     */
    private Integer orderNum;

    /**
     * Child departments
     */
    private List<DeptVO> children;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
