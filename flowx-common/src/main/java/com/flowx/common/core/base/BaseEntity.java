package com.flowx.common.core.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity base class
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary ID (Snowflake algorithm)
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private Long id;

    /**
     * Tenant ID
     */
    @Column("tenant_id")
    private Long tenantId;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(value = "create_time", onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * Update time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(value = "update_time", onUpdateValue = "now()")
    private LocalDateTime updateTime;

    /**
     * Creator ID
     */
    @Column(value = "create_by", onInsertValue = "0")
    private Long createBy;

    /**
     * Updater ID
     */
    @Column(value = "update_by", onUpdateValue = "0")
    private Long updateBy;

    /**
     * Deleted flag (0=normal, 1=deleted)
     */
    @Column(value = "deleted", isLogicDelete = true)
    private Integer deleted;
}
