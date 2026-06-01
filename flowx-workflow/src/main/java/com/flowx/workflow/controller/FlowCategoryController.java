package com.flowx.workflow.controller;

import com.flowx.common.core.result.R;
import com.flowx.workflow.dto.FlowCategoryDTO;
import com.flowx.workflow.service.FlowCategoryService;
import com.flowx.workflow.vo.FlowCategoryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Flow category management controller
 *
 * @author FlowX
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/flow/categories")
@RequiredArgsConstructor
public class FlowCategoryController {

    private final FlowCategoryService categoryService;

    /**
     * Get category by ID
     *
     * @param id category ID
     * @return category VO
     */
    @GetMapping("/{id}")
    public R<FlowCategoryVO> getCategoryById(@PathVariable("id") Long id) {
        FlowCategoryVO vo = categoryService.getCategoryById(id);
        return R.ok(vo);
    }

    /**
     * List all categories
     *
     * @return list of category VOs
     */
    @GetMapping("/list")
    public R<List<FlowCategoryVO>> listCategories() {
        List<FlowCategoryVO> list = categoryService.listCategories();
        return R.ok(list);
    }

    /**
     * Create new category
     *
     * @param dto category creation DTO
     * @return created category ID
     */
    @PostMapping
    public R<Long> createCategory(@Valid @RequestBody FlowCategoryDTO dto) {
        Long categoryId = categoryService.createCategory(dto);
        return R.ok(categoryId);
    }

    /**
     * Update existing category
     *
     * @param id  category ID
     * @param dto category update DTO
     * @return success response
     */
    @PutMapping("/{id}")
    public R<Void> updateCategory(@PathVariable("id") Long id, @Valid @RequestBody FlowCategoryDTO dto) {
        categoryService.updateCategory(id, dto);
        return R.ok();
    }

    /**
     * Delete category
     *
     * @param id category ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return R.ok();
    }
}
