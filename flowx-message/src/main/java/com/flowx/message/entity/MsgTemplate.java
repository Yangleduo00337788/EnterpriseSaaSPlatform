package com.flowx.message.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
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
@Table("msg_template")
public class MsgTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Template name
     */
    @Column("template_name")
    private String templateName;

    /**
     * Template code (unique identifier)
     */
    @Column("template_code")
    private String templateCode;

    /**
     * Template type: email/sms/wechat/dingtalk
     */
    @Column("template_type")
    private String templateType;

    /**
     * Title template (supports placeholder like {variable})
     */
    @Column("title_template")
    private String titleTemplate;

    /**
     * Content template (supports placeholder like {variable})
     */
    @Column("content_template")
    private String contentTemplate;

    /**
     * Status (0=disabled, 1=enabled)
     */
    @Column("status")
    private Integer status;

    /**
     * Parameters description (JSON format, describes available placeholders)
     */
    @Column("params_desc")
    private String paramsDesc;
}
