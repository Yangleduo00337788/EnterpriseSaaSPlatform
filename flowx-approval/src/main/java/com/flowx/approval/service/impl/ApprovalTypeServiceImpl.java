package com.flowx.approval.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flowx.approval.dto.ApprovalTypeDTO;
import com.flowx.approval.entity.ApprovalType;
import com.flowx.approval.mapper.ApprovalTypeMapper;
import com.flowx.approval.service.ApprovalTypeService;
import com.flowx.approval.vo.ApprovalTypeVO;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Approval type service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalTypeServiceImpl implements ApprovalTypeService {

    private final ApprovalTypeMapper typeMapper;

    @Override
    public ApprovalTypeVO getTypeById(Long typeId) {
        AssertUtil.notNull(typeId, "审批类型ID不能为空");
        ApprovalType type = typeMapper.selectById(typeId);
        AssertUtil.notNull(type, ResultCodeEnum.NOT_FOUND.getCode(), "审批类型不存在");
        return convertToVO(type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createType(ApprovalTypeDTO dto) {
        AssertUtil.notNull(dto, "审批类型信息不能为空");
        AssertUtil.notBlank(dto.getTypeName(), "审批类型名称不能为空");
        AssertUtil.notBlank(dto.getTypeCode(), "审批类型编码不能为空");
        AssertUtil.notBlank(dto.getFlowKey(), "关联流程标识不能为空");

        // Check duplicate type code
        QueryWrapper<ApprovalType> wrapper = new QueryWrapper<>();
        wrapper.eq("type_code", dto.getTypeCode());
        Long count = typeMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("审批类型编码已存在");
        }

        ApprovalType type = new ApprovalType();
        BeanUtils.copyProperties(dto, type);

        // Set defaults
        if (type.getSort() == null) {
            type.setSort(0);
        }
        if (type.getStatus() == null) {
            type.setStatus(1);
        }

        typeMapper.insert(type);
        log.info("Created approval type: {}", type.getTypeName());
        return type.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateType(Long typeId, ApprovalTypeDTO dto) {
        AssertUtil.notNull(typeId, "审批类型ID不能为空");
        AssertUtil.notNull(dto, "审批类型信息不能为空");

        ApprovalType type = typeMapper.selectById(typeId);
        AssertUtil.notNull(type, ResultCodeEnum.NOT_FOUND.getCode(), "审批类型不存在");

        // Check duplicate type code (exclude self)
        if (dto.getTypeCode() != null && !dto.getTypeCode().equals(type.getTypeCode())) {
            QueryWrapper<ApprovalType> wrapper = new QueryWrapper<>();
            wrapper.eq("type_code", dto.getTypeCode());
            wrapper.ne("id", typeId);
            Long count = typeMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BizException("审批类型编码已存在");
            }
        }

        BeanUtils.copyProperties(dto, type, "id", "createTime", "createBy", "tenantId", "deleted");
        typeMapper.updateById(type);
        log.info("Updated approval type: {}", typeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteType(Long typeId) {
        AssertUtil.notNull(typeId, "审批类型ID不能为空");
        ApprovalType type = typeMapper.selectById(typeId);
        AssertUtil.notNull(type, ResultCodeEnum.NOT_FOUND.getCode(), "审批类型不存在");

        // Soft delete
        typeMapper.deleteById(typeId);
        log.info("Deleted approval type: {}", typeId);
    }

    @Override
    public List<ApprovalTypeVO> listTypes() {
        QueryWrapper<ApprovalType> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByAsc("sort", "create_time");
        List<ApprovalType> types = typeMapper.selectList(wrapper);
        return types.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * Convert entity to VO
     */
    private ApprovalTypeVO convertToVO(ApprovalType type) {
        ApprovalTypeVO vo = new ApprovalTypeVO();
        BeanUtils.copyProperties(type, vo);
        return vo;
    }
}
