package com.flowx.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Config name
     */
    @TableField("config_name")
    private String configName;

    /**
     * Config key (unique)
     */
    @TableField("config_key")
    private String configKey;

    /**
     * Config value
     */
    @TableField("config_value")
    private String configValue;

    /**
     * Config type (Y=system built-in, N=user defined)
     */
    @TableField("config_type")
    private String configType;

    /**
     * Remark
     */
    @TableField("remark")
    private String remark;
}
