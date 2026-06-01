package com.flowx.user.service;

import com.flowx.user.dto.DeptDTO;
import com.flowx.user.vo.DeptVO;

import java.util.List;

/**
 * Department service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface DeptService {

    /**
     * Get department by ID
     *
     * @param deptId department ID
     * @return department VO
     */
    DeptVO getDeptById(Long deptId);

    /**
     * Create new department
     *
     * @param dto department creation DTO
     * @return created department ID
     */
    Long createDept(DeptDTO dto);

    /**
     * Update existing department
     *
     * @param deptId department ID
     * @param dto    department update DTO
     */
    void updateDept(Long deptId, DeptDTO dto);

    /**
     * Delete department (soft delete)
     *
     * @param deptId department ID
     */
    void deleteDept(Long deptId);

    /**
     * List all departments (flat)
     *
     * @return list of department VOs
     */
    List<DeptVO> listDepts();

    /**
     * Get department tree structure
     *
     * @return tree-structured department VOs
     */
    List<DeptVO> getDeptTree();
}
