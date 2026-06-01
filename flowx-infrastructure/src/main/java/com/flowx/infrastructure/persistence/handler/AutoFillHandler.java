package com.flowx.infrastructure.persistence.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Flex auto-fill handler.
 * Automatically populates createTime, updateTime, createBy, updateBy, deleted fields.
 */
@Slf4j
@Component
public class AutoFillHandler implements MetaObjectHandler {

    private static final String FIELD_CREATE_TIME = "createTime";
    private static final String FIELD_UPDATE_TIME = "updateTime";
    private static final String FIELD_CREATE_BY = "createBy";
    private static final String FIELD_UPDATE_BY = "updateBy";
    private static final String FIELD_DELETED = "deleted";

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("Auto-fill insert fields");
        LocalDateTime now = LocalDateTime.now();
        String currentUser = getCurrentUser();

        this.strictInsertFill(metaObject, FIELD_CREATE_TIME, LocalDateTime.class, now);
        this.strictInsertFill(metaObject, FIELD_UPDATE_TIME, LocalDateTime.class, now);
        this.strictInsertFill(metaObject, FIELD_CREATE_BY, String.class, currentUser);
        this.strictInsertFill(metaObject, FIELD_UPDATE_BY, String.class, currentUser);
        this.strictInsertFill(metaObject, FIELD_DELETED, Integer.class, 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("Auto-fill update fields");
        this.strictUpdateFill(metaObject, FIELD_UPDATE_TIME, LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, FIELD_UPDATE_BY, String.class, getCurrentUser());
    }

    /**
     * Get current logged-in user identifier.
     * Falls back to "system" if no user context is available.
     */
    private String getCurrentUser() {
        try {
            // Attempt to get user from SecurityUtil (flowx-common)
            // This uses reflection-like approach via static method call
            return cn.hutool.core.util.ReflectUtil.invokeStatic(
                    cn.hutool.core.util.ClassLoaderUtil.loadClass("com.flowx.common.utils.SecurityUtil"),
                    "getCurrentUsername"
            );
        } catch (Exception e) {
            log.trace("No current user context available, defaulting to 'system'");
            return "system";
        }
    }
}
