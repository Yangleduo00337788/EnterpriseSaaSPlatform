package com.flowx.workflow.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.workflow.dto.FlowCategoryDTO;
import com.flowx.workflow.entity.FlowCategory;
import com.flowx.workflow.mapper.FlowCategoryMapper;
import com.flowx.workflow.service.FlowCategoryService;
import com.flowx.workflow.vo.FlowCategoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Flow category service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowCategoryServiceImpl implements FlowCategoryService {

    private final FlowCategoryMapper categoryMapper;

    @Override
    public FlowCategoryVO getCategoryById(Long categoryId) {
        AssertUtil.notNull(categoryId, "分类ID不能为空");
        FlowCategory category = categoryMapper.selectOneById(categoryId);
        AssertUtil.notNull(category, ResultCodeEnum.NOT_FOUND.getCode(), "流程分类不存在");
        return convertToVO(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(FlowCategoryDTO dto) {
        AssertUtil.notNull(dto, "分类信息不能为空");
        AssertUtil.notBlank(dto.getCategoryName(), "分类名称不能为空");
        AssertUtil.notBlank(dto.getCategoryCode(), "分类编码不能为空");

        // Check duplicate category code
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("category_code", dto.getCategoryCode());
        Long count = categoryMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("分类编码已存在");
        }

        FlowCategory category = new FlowCategory();
        BeanUtils.copyProperties(dto, category);

        // Set defaults
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }

        categoryMapper.insert(category);
        log.info("Created flow category: {}", category.getCategoryName());
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long categoryId, FlowCategoryDTO dto) {
        AssertUtil.notNull(categoryId, "分类ID不能为空");
        AssertUtil.notNull(dto, "分类信息不能为空");

        FlowCategory category = categoryMapper.selectOneById(categoryId);
        AssertUtil.notNull(category, ResultCodeEnum.NOT_FOUND.getCode(), "流程分类不存在");

        // Check duplicate category code (exclude self)
        if (dto.getCategoryCode() != null && !dto.getCategoryCode().equals(category.getCategoryCode())) {
            QueryWrapper wrapper = QueryWrapper.create();
            wrapper.eq("category_code", dto.getCategoryCode());
            wrapper.ne("id", categoryId);
            Long count = categoryMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BizException("分类编码已存在");
            }
        }

        BeanUtils.copyProperties(dto, category, "id", "createTime", "createBy", "tenantId", "deleted");
        categoryMapper.updateById(category);
        log.info("Updated flow category: {}", categoryId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long categoryId) {
        AssertUtil.notNull(categoryId, "分类ID不能为空");
        FlowCategory category = categoryMapper.selectOneById(categoryId);
        AssertUtil.notNull(category, ResultCodeEnum.NOT_FOUND.getCode(), "流程分类不存在");

        // Soft delete
        categoryMapper.deleteById(categoryId);
        log.info("Deleted flow category: {}", categoryId);
    }

    @Override
    public List<FlowCategoryVO> listCategories() {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.orderBy("sort", true).orderBy("create_time", true);
        List<FlowCategory> categories = categoryMapper.selectList(wrapper);
        return categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * Convert entity to VO
     */
    private FlowCategoryVO convertToVO(FlowCategory category) {
        FlowCategoryVO vo = new FlowCategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
}
