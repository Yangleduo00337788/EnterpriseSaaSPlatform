package com.flowx.infrastructure.persistence.handler;

import com.flowx.common.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Utility component for auto-fill operations.
 * MyBatis-Flex handles static auto-fill (createTime, updateTime) via @Column annotations.
 * This component provides dynamic user context (createBy, updateBy) for use in service layers.
 */
@Slf4j
@Component
public class AutoFillHandler {

    /**
     * Get current user ID for auto-fill, falls back to 0L if no user context.
     *
     * @return current user ID or 0L
     */
    public Long getCurrentUserId() {
        try {
            Long userId = SecurityUtil.getUserId();
            return userId != null ? userId : 0L;
        } catch (Exception e) {
            log.trace("No current user context available, defaulting to 0");
            return 0L;
        }
    }
}