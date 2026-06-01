package com.flowx.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Message template entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("msg_template")
public class MsgTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Template name
     */
    @TableField("template_name")
    private String templateName;

    /**
     * Template code (unique identifier)
     */
    @TableField("template_code")
    private String templateCode;

    /**
     * Template type: email/sms/wechat/dingtalk
     */
    @TableField("template_type")
    private String templateType;

    /**
     * Title template (supports placeholder like {variable})
     */
    @TableField("title_template")
    private String titleTemplate;

    /**
     * Content template (supports placeholder like {variable})
     */
    @TableField("content_template")
    private String contentTemplate;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @TableField("status")
    private Integer status;

    /**
     * Parameters description (JSON format, describes available placeholders)
     */
    @TableField("params_desc")
    private String paramsDesc;
}
