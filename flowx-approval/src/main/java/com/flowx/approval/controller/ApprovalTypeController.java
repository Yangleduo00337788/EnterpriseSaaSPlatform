package com.flowx.approval.controller;

import com.flowx.approval.dto.ApprovalTypeDTO;
import com.flowx.approval.service.ApprovalTypeService;
import com.flowx.approval.vo.ApprovalTypeVO;
import com.flowx.common.core.result.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Approval type management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/approval/type")
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
     * Update existing approval type (id from request body)
     *
     * @param dto type update DTO
     * @return success response
     */
    @PutMapping
    public R<Void> updateType(@Valid @RequestBody ApprovalTypeDTO dto) {
        typeService.updateType(dto.getId(), dto);
        return R.ok();
    }

    /**
     * Delete approval types by IDs (comma-separated)
     *
     * @param ids type IDs
     * @return success response
     */
    @DeleteMapping("/{ids}")
    public R<Void> deleteTypes(@PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        for (Long id : idList) {
            typeService.deleteType(id);
        }
        return R.ok();
    }
}