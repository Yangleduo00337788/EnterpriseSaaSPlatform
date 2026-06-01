package com.flowx.workflow.service;

import com.flowx.workflow.dto.FlowCategoryDTO;
import com.flowx.workflow.vo.FlowCategoryVO;

import java.util.List;

/**
 * Flow category service interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface FlowCategoryService {

    /**
     * Get category by ID
     *
     * @param categoryId category ID
     * @return category VO
     */
    FlowCategoryVO getCategoryById(Long categoryId);

    /**
     * Create new category
     *
     * @param dto category creation DTO
     * @return created category ID
     */
    Long createCategory(FlowCategoryDTO dto);

    /**
     * Update existing category
     *
     * @param categoryId category ID
     * @param dto        category update DTO
     */
    void updateCategory(Long categoryId, FlowCategoryDTO dto);

    /**
     * Delete category (soft delete)
     *
     * @param categoryId category ID
     */
    void deleteCategory(Long categoryId);

    /**
     * List all categories
     *
     * @return list of category VOs
     */
    List<FlowCategoryVO> listCategories();
}
