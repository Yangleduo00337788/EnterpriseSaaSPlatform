package com.flowcloud.notification.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table("sys_message")
public class SysMessage {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column(tenantId = true)
    private Long tenantId;

    private Long userId;
    private String title;
    private String content;
    private String type;
    private String bizType;
    private Long bizId;
    private Integer isRead;
    private LocalDateTime createTime;
}
