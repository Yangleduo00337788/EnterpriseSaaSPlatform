package com.flowx.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * System config create/update DTO
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class ConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Config name
     */
    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称长度不能超过100个字符")
    private String configName;

    /**
     * Config key (unique)
     */
    @NotBlank(message = "参数键名不能为空")
    @Size(max = 100, message = "参数键名长度不能超过100个字符")
    private String configKey;

    /**
     * Config value
     */
    @NotBlank(message = "参数键值不能为空")
    @Size(max = 500, message = "参数键值长度不能超过500个字符")
    private String configValue;

    /**
     * Config type (Y=system built-in, N=user defined)
     */
    private String configType;

    /**
     * Remark
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
