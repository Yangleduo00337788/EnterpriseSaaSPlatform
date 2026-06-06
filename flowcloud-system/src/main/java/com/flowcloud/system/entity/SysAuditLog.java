package com.flowcloud.system.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table("sys_audit_log")
public class SysAuditLog {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long tenantId;
    private Long userId;
    private String userName;
    private String action;
    private String targetType;
    private String targetId;
    private String targetName;
    private String result;
    private String detail;
    private String ip;
    private LocalDateTime createTime;
}