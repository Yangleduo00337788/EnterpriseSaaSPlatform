package com.flowx.user.controller;

import com.flowx.common.core.result.R;
import com.flowx.user.dto.DeptDTO;
import com.flowx.user.service.DeptService;
import com.flowx.user.vo.DeptVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Department management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/depts")
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
     * Update existing department
     *
     * @param id  department ID
     * @param dto department update DTO
     * @return success response
     */
    @PutMapping("/{id}")
    public R<Void> updateDept(@PathVariable("id") Long id, @Valid @RequestBody DeptDTO dto) {
        deptService.updateDept(id, dto);
        return R.ok();
    }

    /**
     * Delete department
     *
     * @param id department ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteDept(@PathVariable("id") Long id) {
        deptService.deleteDept(id);
        return R.ok();
    }
}
