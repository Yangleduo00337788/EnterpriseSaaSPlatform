package com.flowx.workflow.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
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
@Table("flow_definition")
public class FlowDefinition extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Definition key (unique identifier for the process)
     */
    @Column("definition_key")
    private String definitionKey;

    /**
     * Definition name
     */
    @Column("definition_name")
    private String definitionName;

    /**
     * Category ID
     */
    @Column("category_id")
    private Long categoryId;

    /**
     * Version number
     */
    @Column("version")
    private Integer version;

    /**
     * Description
     */
    @Column("description")
    private String description;

    /**
     * BPMN XML content
     */
    @Column("bpmn_xml")
    private String bpmnXml;

    /**
     * Form JSON schema
     */
    @Column("form_json")
    private String formJson;

    /**
     * Status (0=draft, 1=active, 2=suspended)
     */
    @Column("status")
    private Integer status;

    /**
     * Deploy time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column("deploy_time")
    private LocalDateTime deployTime;
}
