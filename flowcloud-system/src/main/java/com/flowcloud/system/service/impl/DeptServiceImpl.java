package com.flowcloud.system.service.impl;

import com.flowcloud.common.context.TenantContext;
import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.system.dto.DeptDTO;
import com.flowcloud.system.entity.SysDept;
import com.flowcloud.system.entity.SysUser;
import com.flowcloud.system.mapper.SysDeptMapper;
import com.flowcloud.system.mapper.SysUserMapper;
import com.flowcloud.system.service.DeptService;
import com.flowcloud.system.vo.DeptVO;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final SysDeptMapper deptMapper;
    private final SysUserMapper userMapper;

    @Override
    public List<DeptVO> listTree() {
        List<SysDept> depts = deptMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(SysDept::getTenantId).eq(TenantContext.getTenantId())
                        .orderBy(SysDept::getSort, true)
                        .orderBy(SysDept::getCreateTime, true));
        Map<Long, DeptVO> nodeMap = new LinkedHashMap<>();
        for (SysDept dept : depts) {
            nodeMap.put(dept.getId(), toVO(dept));
        }
        List<DeptVO> roots = new ArrayList<>();
        for (SysDept dept : depts) {
            DeptVO current = nodeMap.get(dept.getId());
            if (dept.getParentId() == null || dept.getParentId() == 0) {
                roots.add(current);
                continue;
            }
            DeptVO parent = nodeMap.get(dept.getParentId());
            if (parent == null) {
                roots.add(current);
                continue;
            }
            parent.getChildren().add(current);
        }
        roots.forEach(this::sortChildren);
        return roots;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DeptDTO dto) {
        SysDept dept = new SysDept();
        dept.setTenantId(TenantContext.getTenantId());
        dept.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        dept.setDeptName(dto.getDeptName());
        dept.setLeader(dto.getLeader());
        dept.setLeaderUserId(dto.getLeaderUserId());
        dept.setSort(dto.getSort() == null ? 0 : dto.getSort());
        dept.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        dept.setAncestors(buildAncestors(dept.getParentId()));
        deptMapper.insert(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DeptDTO dto) {
        SysDept dept = deptMapper.selectOneById(dto.getId());
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }
        dept.setDeptName(dto.getDeptName());
        dept.setLeader(dto.getLeader());
        dept.setLeaderUserId(dto.getLeaderUserId());
        dept.setSort(dto.getSort() == null ? 0 : dto.getSort());
        dept.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        deptMapper.update(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        long childCount = deptMapper.selectCountByQuery(
                QueryWrapper.create().where(SysDept::getParentId).eq(id));
        if (childCount > 0) {
            throw new BusinessException("请先删除子部门");
        }
        long userCount = userMapper.selectCountByQuery(
                QueryWrapper.create().where(SysUser::getDeptId).eq(id));
        if (userCount > 0) {
            throw new BusinessException("该部门下仍有关联员工");
        }
        deptMapper.deleteById(id);
    }

    private String buildAncestors(Long parentId) {
        if (parentId == null || parentId == 0) {
            return "0";
        }
        SysDept parent = deptMapper.selectOneById(parentId);
        if (parent == null) {
            return "0";
        }
        return parent.getAncestors() + "," + parent.getId();
    }

    private DeptVO toVO(SysDept dept) {
        DeptVO vo = new DeptVO();
        vo.setId(dept.getId());
        vo.setParentId(dept.getParentId());
        vo.setDeptName(dept.getDeptName());
        vo.setLeader(dept.getLeader());
        vo.setLeaderUserId(dept.getLeaderUserId());
        vo.setAncestors(dept.getAncestors());
        vo.setSort(dept.getSort());
        vo.setStatus(dept.getStatus());
        return vo;
    }

    private void sortChildren(DeptVO node) {
        node.getChildren().sort(Comparator.comparing(item -> item.getSort() == null ? 0 : item.getSort()));
        node.getChildren().forEach(this::sortChildren);
    }
}