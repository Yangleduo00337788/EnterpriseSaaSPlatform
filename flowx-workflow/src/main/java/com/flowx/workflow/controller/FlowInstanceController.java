package com.flowx.workflow.controller;

import com.flowx.common.core.result.PageResult;
import com.flowx.common.core.result.R;
import com.flowx.workflow.dto.FlowInstanceDTO;
import com.flowx.workflow.dto.FlowInstanceQueryDTO;
import com.flowx.workflow.service.FlowInstanceService;
import com.flowx.workflow.vo.FlowInstanceVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Flow instance management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/workflow/instance")
@RequiredArgsConstructor
public class FlowInstanceController {

    private final FlowInstanceService instanceService;

    /**
     * Start a new process instance
     *
     * @param dto instance start DTO
     * @return created instance ID
     */
    @PostMapping("/start")
    public R<Long> startProcess(@Valid @RequestBody FlowInstanceDTO dto) {
        Long instanceId = instanceService.startProcess(dto);
        return R.ok(instanceId);
    }

    /**
     * List instances with pagination
     *
     * @param queryDTO query parameters
     * @return paginated instance list
     */
    @GetMapping("/list")
    public R<PageResult<FlowInstanceVO>> getInstances(FlowInstanceQueryDTO queryDTO) {
        PageResult<FlowInstanceVO> result = instanceService.getInstances(queryDTO);
        return R.ok(result);
    }

    /**
     * Get instance detail with task history
     *
     * @param id instance ID
     * @return instance detail VO
     */
    @GetMapping("/{id}")
    public R<FlowInstanceVO> getInstanceDetail(@PathVariable("id") Long id) {
        FlowInstanceVO vo = instanceService.getInstanceDetail(id);
        return R.ok(vo);
    }

    /**
     * Cancel a running instance
     *
     * @param id instance ID
     * @return success response
     */
    @PutMapping("/{id}/cancel")
    public R<Void> cancelInstance(@PathVariable("id") Long id) {
        instanceService.cancelInstance(id);
        return R.ok();
    }
}