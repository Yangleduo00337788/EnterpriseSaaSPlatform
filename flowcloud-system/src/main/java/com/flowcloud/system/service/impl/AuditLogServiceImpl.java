package com.flowcloud.system.service.impl;

import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.result.PageResult;
import com.flowcloud.system.entity.SysAuditLog;
import com.flowcloud.system.mapper.SysAuditLogMapper;
import com.flowcloud.system.service.AuditLogService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final SysAuditLogMapper auditLogMapper;

    @Override
    public void record(SysAuditLog log) {
        auditLogMapper.insert(log);
    }

    @Override
    public PageResult<SysAuditLog> page(String action, String targetType, Long userId,
                                        LocalDateTime startTime, LocalDateTime endTime,
                                        int pageNum, int pageSize) {
        QueryWrapper query = QueryWrapper.create()
                .where(SysAuditLog::getTenantId).eq(TenantContext.getTenantId());
        if (StringUtils.hasText(action)) {
            query.and(SysAuditLog::getAction).like(action);
        }
        if (StringUtils.hasText(targetType)) {
            query.and(SysAuditLog::getTargetType).eq(targetType);
        }
        if (userId != null) {
            query.and(SysAuditLog::getUserId).eq(userId);
        }
        if (startTime != null) {
            query.and(SysAuditLog::getCreateTime).ge(startTime);
        }
        if (endTime != null) {
            query.and(SysAuditLog::getCreateTime).le(endTime);
        }
        query.orderBy(SysAuditLog::getCreateTime, false);
        Page<SysAuditLog> p = auditLogMapper.paginate(pageNum, pageSize, query);
        return PageResult.of(p.getRecords(), p.getTotalRow(), pageNum, pageSize);
    }
}