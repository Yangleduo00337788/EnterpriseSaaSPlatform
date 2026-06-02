package com.flowx.workflow.controller;

import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import com.flowx.workflow.dto.FlowDefinitionDTO;
import com.flowx.workflow.dto.FlowDeployDTO;
import com.flowx.workflow.service.FlowDefinitionService;
import com.flowx.workflow.vo.FlowDefinitionVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Flow definition management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/workflow/definition")
@RequiredArgsConstructor
public class FlowDefinitionController {

    private final FlowDefinitionService definitionService;

    /**
     * Get definition by ID
     *
     * @param id definition ID
     * @return definition VO
     */
    @GetMapping("/{id}")
    public R<FlowDefinitionVO> getDefinitionById(@PathVariable("id") Long id) {
        FlowDefinitionVO vo = definitionService.getDefinitionById(id);
        return R.ok(vo);
    }

    /**
     * Get definition by key
     *
     * @param key definition key
     * @return definition VO
     */
    @GetMapping("/key/{key}")
    public R<FlowDefinitionVO> getDefinitionByKey(@PathVariable("key") String key) {
        FlowDefinitionVO vo = definitionService.getDefinitionByKey(key);
        return R.ok(vo);
    }

    /**
     * List definitions with pagination
     *
     * @param pageNum    page number
     * @param pageSize   page size
     * @param categoryId optional category filter
     * @param status     optional status filter
     * @return paginated definition list
     */
    @GetMapping("/list")
    public R<PageResult<FlowDefinitionVO>> listDefinitions(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) Integer status) {
        PageResult<FlowDefinitionVO> result = definitionService.listDefinitions(pageNum, pageSize, categoryId, status);
        return R.ok(result);
    }

    /**
     * Create new definition
     *
     * @param dto definition creation DTO
     * @return created definition ID
     */
    @PostMapping
    public R<Long> createDefinition(@Valid @RequestBody FlowDefinitionDTO dto) {
        Long definitionId = definitionService.createDefinition(dto);
        return R.ok(definitionId);
    }

    /**
     * Update existing definition (id from request body)
     *
     * @param dto definition update DTO
     * @return success response
     */
    @PutMapping
    public R<Void> updateDefinition(@Valid @RequestBody FlowDefinitionDTO dto) {
        definitionService.updateDefinition(dto.getId(), dto);
        return R.ok();
    }

    /**
     * Delete definitions by IDs (comma-separated)
     *
     * @param ids definition IDs
     * @return success response
     */
    @DeleteMapping("/{ids}")
    public R<Void> deleteDefinitions(@PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        for (Long id : idList) {
            definitionService.deleteDefinition(id);
        }
        return R.ok();
    }

    /**
     * Deploy BPMN XML to Flowable engine
     *
     * @param dto deploy DTO
     * @return success response
     */
    @PostMapping("/deploy")
    public R<Void> deploy(@Valid @RequestBody FlowDeployDTO dto) {
        definitionService.deploy(dto);
        return R.ok();
    }

    /**
     * Suspend a flow definition
     *
     * @param id definition ID
     * @return success response
     */
    @PutMapping("/{id}/suspend")
    public R<Void> suspend(@PathVariable("id") Long id) {
        definitionService.suspend(id);
        return R.ok();
    }

    /**
     * Activate a suspended flow definition
     *
     * @param id definition ID
     * @return success response
     */
    @PutMapping("/{id}/activate")
    public R<Void> activate(@PathVariable("id") Long id) {
        definitionService.activate(id);
        return R.ok();
    }
}