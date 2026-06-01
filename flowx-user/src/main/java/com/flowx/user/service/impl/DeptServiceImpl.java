package com.flowx.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.ResultCodeEnum;
import com.flowx.common.util.AssertUtil;
import com.flowx.user.convert.DeptConvert;
import com.flowx.user.dto.DeptDTO;
import com.flowx.user.entity.SysDept;
import com.flowx.user.mapper.SysDeptMapper;
import com.flowx.user.service.DeptService;
import com.flowx.user.vo.DeptVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Department service implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final SysDeptMapper deptMapper;
    private final DeptConvert deptConvert;

    @Override
    public DeptVO getDeptById(Long deptId) {
        AssertUtil.notNull(deptId, "部门ID不能为空");
        SysDept dept = deptMapper.selectById(deptId);
        AssertUtil.notNull(dept, ResultCodeEnum.DEPT_NOT_FOUND.getCode(), ResultCodeEnum.DEPT_NOT_FOUND.getMessage());
        return deptConvert.toVO(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDept(DeptDTO dto) {
        AssertUtil.notNull(dto, "部门信息不能为空");
        AssertUtil.notBlank(dto.getDeptName(), "部门名称不能为空");

        SysDept dept = deptConvert.toEntity(dto);

        // Set defaults
        if (dept.getSort() == null) {
            dept.setSort(0);
        }
        if (dept.getOrderNum() == null) {
            dept.setOrderNum(0);
        }
        if (dept.getStatus() == null) {
            dept.setStatus(1);
        }

        deptMapper.insert(dept);
        log.info("Created department: {}", dept.getDeptName());
        return dept.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(Long deptId, DeptDTO dto) {
        AssertUtil.notNull(deptId, "部门ID不能为空");
        AssertUtil.notNull(dto, "部门信息不能为空");

        SysDept dept = deptMapper.selectById(deptId);
        AssertUtil.notNull(dept, ResultCodeEnum.DEPT_NOT_FOUND.getCode(), ResultCodeEnum.DEPT_NOT_FOUND.getMessage());

        // Prevent setting parent to self
        if (dto.getParentId() != null && dto.getParentId().equals(deptId)) {
            throw new BizException("父部门不能是自身");
        }

        deptConvert.updateEntity(dto, dept);
        deptMapper.updateById(dept);
        log.info("Updated department: {}", deptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDept(Long deptId) {
        AssertUtil.notNull(deptId, "部门ID不能为空");
        SysDept dept = deptMapper.selectById(deptId);
        AssertUtil.notNull(dept, ResultCodeEnum.DEPT_NOT_FOUND.getCode(), ResultCodeEnum.DEPT_NOT_FOUND.getMessage());

        // Check if dept has children
        QueryWrapper<SysDept> childWrapper = new QueryWrapper<>();
        childWrapper.eq("parent_id", deptId);
        Long childCount = deptMapper.selectCount(childWrapper);
        if (childCount > 0) {
            throw new BizException("存在子部门，不允许删除");
        }

        // Soft delete
        deptMapper.deleteById(deptId);
        log.info("Deleted department: {}", deptId);
    }

    @Override
    public List<DeptVO> listDepts() {
        QueryWrapper<SysDept> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("parent_id", "sort", "order_num");
        List<SysDept> depts = deptMapper.selectList(wrapper);
        return deptConvert.toVOList(depts);
    }

    @Override
    public List<DeptVO> getDeptTree() {
        QueryWrapper<SysDept> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort", "order_num");
        List<SysDept> allDepts = deptMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(allDepts)) {
            return Collections.emptyList();
        }

        List<DeptVO> voList = deptConvert.toVOList(allDepts);
        return buildDeptTree(voList, 0L);
    }

    /**
     * Build department tree recursively
     */
    private List<DeptVO> buildDeptTree(List<DeptVO> depts, Long parentId) {
        List<DeptVO> tree = new ArrayList<>();
        for (DeptVO dept : depts) {
            if (parentId.equals(dept.getParentId())) {
                List<DeptVO> children = buildDeptTree(depts, dept.getId());
                dept.setChildren(children);
                tree.add(dept);
            }
        }
        return tree;
    }
}
