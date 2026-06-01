package com.flowx.approval.controller;

import com.flowx.approval.dto.ApprovalTypeDTO;
import com.flowx.approval.service.ApprovalTypeService;
import com.flowx.approval.vo.ApprovalTypeVO;
import com.flowx.common.core.result.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Approval type management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/approval/types")
@RequiredArgsConstructor
public class ApprovalTypeController {

    private final ApprovalTypeService typeService;

    /**
     * Get approval type by ID
     *
     * @param id type ID
     * @return type VO
     */
    @GetMapping("/{id}")
    public R<ApprovalTypeVO> getTypeById(@PathVariable("id") Long id) {
        ApprovalTypeVO vo = typeService.getTypeById(id);
        return R.ok(vo);
    }

    /**
     * List all approval types
     *
     * @return list of type VOs
     */
    @GetMapping("/list")
    public R<List<ApprovalTypeVO>> listTypes() {
        List<ApprovalTypeVO> list = typeService.listTypes();
        return R.ok(list);
    }

    /**
     * Create new approval type
     *
     * @param dto type creation DTO
     * @return created type ID
     */
    @PostMapping
    public R<Long> createType(@Valid @RequestBody ApprovalTypeDTO dto) {
        Long typeId = typeService.createType(dto);
        return R.ok(typeId);
    }

    /**
     * Update existing approval type
     *
     * @param id  type ID
     * @param dto type update DTO
     * @return success response
     */
    @PutMapping("/{id}")
    public R<Void> updateType(@PathVariable("id") Long id, @Valid @RequestBody ApprovalTypeDTO dto) {
        typeService.updateType(id, dto);
        return R.ok();
    }

    /**
     * Delete approval type
     *
     * @param id type ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteType(@PathVariable("id") Long id) {
        typeService.deleteType(id);
        return R.ok();
    }
}
