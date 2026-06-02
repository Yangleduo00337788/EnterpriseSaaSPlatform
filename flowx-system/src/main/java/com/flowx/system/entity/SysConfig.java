package com.flowx.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * System configuration entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_config")
public class SysConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Config name
     */
    @Column("config_name")
    private String configName;

    /**
     * Config key (unique)
     */
    @Column("config_key")
    private String configKey;

    /**
     * Config value
     */
    @Column("config_value")
    private String configValue;

    /**
     * Config type (Y=system built-in, N=user defined)
     */
    @Column("config_type")
    private String configType;

    /**
     * Remark
     */
    @Column("remark")
    private String remark;
}