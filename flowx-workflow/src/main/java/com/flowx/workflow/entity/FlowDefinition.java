package com.flowx.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowx.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Flow definition entity
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("flow_definition")
public class FlowDefinition extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Definition key (unique identifier for the process)
     */
    @TableField("definition_key")
    private String definitionKey;

    /**
     * Definition name
     */
    @TableField("definition_name")
    private String definitionName;

    /**
     * Category ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * Version number
     */
    @TableField("version")
    private Integer version;

    /**
     * Description
     */
    @TableField("description")
    private String description;

    /**
     * BPMN XML content
     */
    @TableField("bpmn_xml")
    private String bpmnXml;

    /**
     * Form JSON schema
     */
    @TableField("form_json")
    private String formJson;

    /**
     * Status (0=draft, 1=active, 2=suspended)
     */
    @TableField("status")
    private Integer status;

    /**
     * Deploy time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("deploy_time")
    private LocalDateTime deployTime;
}
