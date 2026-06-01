package com.flowx.workflow.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Flow definition view object
 *
 * @author FlowX
 * @since 1.0.0
 */
@Data
public class FlowDefinitionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Definition ID
     */
    private Long id;

    /**
     * Definition key
     */
    private String definitionKey;

    /**
     * Definition name
     */
    private String definitionName;

    /**
     * Category ID
     */
    private Long categoryId;

    /**
     * Category name
     */
    private String categoryName;

    /**
     * Version number
     */
    private Integer version;

    /**
     * Description
     */
    private String description;

    /**
     * BPMN XML content
     */
    private String bpmnXml;

    /**
     * Form JSON schema
     */
    private String formJson;

    /**
     * Status (0=draft, 1=active, 2=suspended)
     */
    private Integer status;

    /**
     * Deploy time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deployTime;

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
