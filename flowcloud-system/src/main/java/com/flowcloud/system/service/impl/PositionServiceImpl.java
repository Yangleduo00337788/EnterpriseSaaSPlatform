package com.flowcloud.system.service.impl;

import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.system.dto.PositionDTO;
import com.flowcloud.system.entity.SysDept;
import com.flowcloud.system.entity.SysPosition;
import com.flowcloud.system.entity.SysUserPosition;
import com.flowcloud.system.mapper.SysDeptMapper;
import com.flowcloud.system.mapper.SysPositionMapper;
import com.flowcloud.system.mapper.SysUserPositionMapper;
import com.flowcloud.system.service.PositionService;
import com.flowcloud.system.vo.PositionVO;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final SysPositionMapper positionMapper;
    private final SysDeptMapper deptMapper;
    private final SysUserPositionMapper userPositionMapper;

    @Override
    public List<PositionVO> listAll() {
        List<SysPosition> positions = positionMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(SysPosition::getTenantId).eq(TenantContext.getTenantId())
                        .orderBy(SysPosition::getSort, true)
                        .orderBy(SysPosition::getCreateTime, true));

        // 批量查部门名
        List<Long> deptIds = positions.stream()
                .filter(p -> p.getDeptId() != null)
                .map(SysPosition::getDeptId)
                .distinct().toList();
        Map<Long, String> deptNameMap = deptIds.isEmpty() ? Map.of() :
                deptMapper.selectListByQuery(
                        QueryWrapper.create().where(SysDept::getId).in(deptIds))
                        .stream().collect(Collectors.toMap(SysDept::getId, SysDept::getDeptName));

        return positions.stream().map(p -> {
            PositionVO vo = new PositionVO();
            vo.setId(p.getId());
            vo.setPositionCode(p.getPositionCode());
            vo.setPositionName(p.getPositionName());
            vo.setDeptId(p.getDeptId());
            vo.setDeptName(p.getDeptId() != null ? deptNameMap.get(p.getDeptId()) : null);
            vo.setSort(p.getSort());
            vo.setStatus(p.getStatus());
            vo.setRemark(p.getRemark());
            return vo;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(PositionDTO dto) {
        SysPosition pos = new SysPosition();
        pos.setTenantId(TenantContext.getTenantId());
        pos.setPositionCode(dto.getPositionCode());
        pos.setPositionName(dto.getPositionName());
        pos.setDeptId(dto.getDeptId());
        pos.setSort(dto.getSort() == null ? 0 : dto.getSort());
        pos.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        pos.setRemark(dto.getRemark());
        positionMapper.insert(pos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PositionDTO dto) {
        SysPosition pos = positionMapper.selectOneById(dto.getId());
        if (pos == null) throw new BusinessException("岗位不存在");
        pos.setPositionCode(dto.getPositionCode());
        pos.setPositionName(dto.getPositionName());
        pos.setDeptId(dto.getDeptId());
        pos.setSort(dto.getSort() == null ? 0 : dto.getSort());
        pos.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        pos.setRemark(dto.getRemark());
        positionMapper.update(pos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        long bound = userPositionMapper.selectCountByQuery(
                QueryWrapper.create().where(SysUserPosition::getPositionId).eq(id));
        if (bound > 0) throw new BusinessException("该岗位下仍有关联人员，请先调整");
        positionMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignUserPositions(Long userId, List<Long> positionIds) {
        userPositionMapper.deleteByQuery(
                QueryWrapper.create().where(SysUserPosition::getUserId).eq(userId));
        if (positionIds == null || positionIds.isEmpty()) return;
        for (Long posId : positionIds) {
            SysUserPosition rel = new SysUserPosition();
            rel.setUserId(userId);
            rel.setPositionId(posId);
            userPositionMapper.insert(rel);
        }
    }

    @Override
    public List<Long> getUserPositionIds(Long userId) {
        return userPositionMapper.selectListByQuery(
                        QueryWrapper.create().where(SysUserPosition::getUserId).eq(userId))
                .stream().map(SysUserPosition::getPositionId).toList();
    }
}