package com.flowx.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Role view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class RoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Role ID
     */
    private Long id;

    /**
     * Role name
     */
    private String roleName;

    /**
     * Role key (unique identifier)
     */
    private String roleKey;

    /**
     * Sort order
     */
    private Integer sort;

    /**
     * Data scope (1=all, 2=custom, 3=dept, 4=dept and below, 5=self)
     */
    private Integer dataScope;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;

    /**
     * Remark
     */
    private String remark;

    /**
     * Assigned menu IDs
     */
    private List<Long> menuIds;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
