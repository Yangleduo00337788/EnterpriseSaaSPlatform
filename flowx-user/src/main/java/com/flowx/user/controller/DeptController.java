package com.flowx.user.controller;

import com.flowx.common.core.result.R;
import com.flowx.user.dto.DeptDTO;
import com.flowx.user.service.DeptService;
import com.flowx.user.vo.DeptVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Department management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/system/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    /**
     * Get department by ID
     *
     * @param id department ID
     * @return department VO
     */
    @GetMapping("/{id}")
    public R<DeptVO> getDeptById(@PathVariable("id") Long id) {
        DeptVO deptVO = deptService.getDeptById(id);
        return R.ok(deptVO);
    }

    /**
     * Get department tree structure
     *
     * @return tree-structured department VOs
     */
    @GetMapping("/tree")
    public R<List<DeptVO>> getDeptTree() {
        List<DeptVO> tree = deptService.getDeptTree();
        return R.ok(tree);
    }

    /**
     * List all departments (flat)
     *
     * @return list of department VOs
     */
    @GetMapping("/list")
    public R<List<DeptVO>> listDepts() {
        List<DeptVO> depts = deptService.listDepts();
        return R.ok(depts);
    }

    /**
     * Create new department
     *
     * @param dto department creation DTO
     * @return created department ID
     */
    @PostMapping
    public R<Long> createDept(@Valid @RequestBody DeptDTO dto) {
        Long deptId = deptService.createDept(dto);
        return R.ok(deptId);
    }

    /**
     * Update existing department (id from request body)
     *
     * @param dto department update DTO
     * @return success response
     */
    @PutMapping
    public R<Void> updateDept(@Valid @RequestBody DeptDTO dto) {
        deptService.updateDept(dto.getId(), dto);
        return R.ok();
    }

    /**
     * Delete departments by IDs (comma-separated)
     *
     * @param ids department IDs
     * @return success response
     */
    @DeleteMapping("/{ids}")
    public R<Void> deleteDepts(@PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        for (Long id : idList) {
            deptService.deleteDept(id);
        }
        return R.ok();
    }
}