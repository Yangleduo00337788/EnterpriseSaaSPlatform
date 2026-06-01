package com.flowx.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * System config view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class ConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Config ID
     */
    private Long id;

    /**
     * Config name
     */
    private String configName;

    /**
     * Config key
     */
    private String configKey;

    /**
     * Config value
     */
    private String configValue;

    /**
     * Config type (Y=system built-in, N=user defined)
     */
    private String configType;

    /**
     * Remark
     */
    private String remark;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
