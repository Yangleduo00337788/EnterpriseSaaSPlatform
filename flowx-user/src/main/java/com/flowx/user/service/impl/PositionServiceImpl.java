package com.flowx.user.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.user.convert.PositionConvert;
import com.flowx.user.dto.PositionDTO;
import com.flowx.user.entity.SysPosition;
import com.flowx.user.mapper.SysPositionMapper;
import com.flowx.user.service.PositionService;
import com.flowx.user.vo.PositionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Position service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final SysPositionMapper positionMapper;
    private final PositionConvert positionConvert;

    @Override
    public PositionVO getPositionById(Long positionId) {
        AssertUtil.notNull(positionId, "岗位ID不能为空");
        SysPosition position = positionMapper.selectOneById(positionId);
        AssertUtil.notNull(position, ResultCodeEnum.POST_NOT_FOUND.getCode(), ResultCodeEnum.POST_NOT_FOUND.getMessage());
        return positionConvert.toVO(position);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPosition(PositionDTO dto) {
        AssertUtil.notNull(dto, "岗位信息不能为空");
        AssertUtil.notBlank(dto.getPositionName(), "岗位名称不能为空");
        AssertUtil.notBlank(dto.getPositionCode(), "岗位编码不能为空");

        // Check position code uniqueness
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("position_code", dto.getPositionCode());
        Long count = positionMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("岗位编码已存在");
        }

        SysPosition position = positionConvert.toEntity(dto);

        // Set defaults
        if (position.getSort() == null) {
            position.setSort(0);
        }
        if (position.getStatus() == null) {
            position.setStatus(1);
        }

        positionMapper.insert(position);
        log.info("Created position: {}", position.getPositionName());
        return position.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePosition(Long positionId, PositionDTO dto) {
        AssertUtil.notNull(positionId, "岗位ID不能为空");
        AssertUtil.notNull(dto, "岗位信息不能为空");

        SysPosition position = positionMapper.selectOneById(positionId);
        AssertUtil.notNull(position, ResultCodeEnum.POST_NOT_FOUND.getCode(), ResultCodeEnum.POST_NOT_FOUND.getMessage());

        // Check position code uniqueness if changed
        if (StringUtils.hasText(dto.getPositionCode()) && !dto.getPositionCode().equals(position.getPositionCode())) {
            QueryWrapper wrapper = QueryWrapper.create();
            wrapper.eq("position_code", dto.getPositionCode());
            wrapper.ne("id", positionId);
            Long count = positionMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BizException("岗位编码已存在");
            }
        }

        positionConvert.updateEntity(dto, position);
        positionMapper.updateById(position);
        log.info("Updated position: {}", positionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePosition(Long positionId) {
        AssertUtil.notNull(positionId, "岗位ID不能为空");
        SysPosition position = positionMapper.selectOneById(positionId);
        AssertUtil.notNull(position, ResultCodeEnum.POST_NOT_FOUND.getCode(), ResultCodeEnum.POST_NOT_FOUND.getMessage());

        // Soft delete
        positionMapper.deleteById(positionId);
        log.info("Deleted position: {}", positionId);
    }

    @Override
    public List<PositionVO> listPositions() {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.orderBy("sort", true);
        List<SysPosition> positions = positionMapper.selectList(wrapper);
        return positionConvert.toVOList(positions);
    }
}
