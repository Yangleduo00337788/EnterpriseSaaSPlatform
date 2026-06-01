package com.flowx.message.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Message template view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class MsgTemplateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Template ID
     */
    private Long id;

    /**
     * Template name
     */
    private String templateName;

    /**
     * Template code (unique identifier)
     */
    private String templateCode;

    /**
     * Template type: email/sms/wechat/dingtalk
     */
    private String templateType;

    /**
     * Title template
     */
    private String titleTemplate;

    /**
     * Content template
     */
    private String contentTemplate;

    /**
     * Status (0=disabled, 1=enabled)
     */
    private Integer status;

    /**
     * Parameters description
     */
    private String paramsDesc;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * Update time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
