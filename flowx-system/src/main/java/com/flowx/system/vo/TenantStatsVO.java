package com.flowx.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Tenant statistics view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class TenantStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Total user count
     */
    private Long userCount;

    /**
     * Storage used (bytes)
     */
    private Long storageUsed;

    /**
     * Storage limit (bytes)
     */
    private Long storageLimit;

    /**
     * API call count
     */
    private Long apiCallCount;

    /**
     * Last active time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastActiveTime;
}
