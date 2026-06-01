package com.flowx.workflow.service;

import com.flowx.common.core.result.PageResult;
import com.flowx.workflow.dto.FlowDefinitionDTO;
import com.flowx.workflow.dto.FlowDeployDTO;
import com.flowx.workflow.vo.FlowDefinitionVO;

/**
 * Flow definition service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface FlowDefinitionService {

    /**
     * Get definition by ID
     *
     * @param definitionId definition ID
     * @return definition VO
     */
    FlowDefinitionVO getDefinitionById(Long definitionId);

    /**
     * Create new definition
     *
     * @param dto definition creation DTO
     * @return created definition ID
     */
    Long createDefinition(FlowDefinitionDTO dto);

    /**
     * Update existing definition
     *
     * @param definitionId definition ID
     * @param dto          definition update DTO
     */
    void updateDefinition(Long definitionId, FlowDefinitionDTO dto);

    /**
     * Delete definition (soft delete)
     *
     * @param definitionId definition ID
     */
    void deleteDefinition(Long definitionId);

    /**
     * Deploy BPMN XML to Flowable engine
     *
     * @param dto deploy DTO with definition ID and BPMN XML
     */
    void deploy(FlowDeployDTO dto);

    /**
     * Suspend a flow definition
     *
     * @param definitionId definition ID
     */
    void suspend(Long definitionId);

    /**
     * Activate a suspended flow definition
     *
     * @param definitionId definition ID
     */
    void activate(Long definitionId);

    /**
     * Get definition by key
     *
     * @param key definition key
     * @return definition VO
     */
    FlowDefinitionVO getDefinitionByKey(String key);

    /**
     * List definitions with pagination
     *
     * @param pageNum    page number
     * @param pageSize   page size
     * @param categoryId optional category filter
     * @param status     optional status filter
     * @return paginated definition list
     */
    PageResult<FlowDefinitionVO> listDefinitions(Integer pageNum, Integer pageSize, Long categoryId, Integer status);
}
