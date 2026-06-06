package com.flowcloud.system.service;

import com.flowcloud.common.result.PageResult;
import com.flowcloud.system.entity.SysAuditLog;

public interface AuditLogService {

    void record(SysAuditLog log);

    PageResult<SysAuditLog> page(String action, String targetType, Long userId,
                                 java.time.LocalDateTime startTime, java.time.LocalDateTime endTime,
                                 int pageNum, int pageSize);
}