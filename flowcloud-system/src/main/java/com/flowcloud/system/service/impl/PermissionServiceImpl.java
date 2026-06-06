package com.flowcloud.system.service.impl;

import com.flowcloud.system.entity.SysPermission;
import com.flowcloud.system.mapper.SysPermissionMapper;
import com.flowcloud.system.service.PermissionService;
import com.flowcloud.system.vo.PermissionVO;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final SysPermissionMapper permissionMapper;

    @Override
    public List<PermissionVO> listTree() {
        List<SysPermission> all = permissionMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(SysPermission::getStatus).eq(1)
                        .orderBy(SysPermission::getSort, true));
        Map<Long, List<SysPermission>> childrenMap = all.stream()
                .collect(Collectors.groupingBy(p -> p.getParentId() == null ? 0L : p.getParentId()));
        return buildTree(childrenMap, 0L);
    }

    private List<PermissionVO> buildTree(Map<Long, List<SysPermission>> childrenMap, Long parentId) {
        List<SysPermission> children = childrenMap.getOrDefault(parentId, List.of());
        List<PermissionVO> result = new ArrayList<>();
        for (SysPermission perm : children) {
            PermissionVO vo = toVO(perm);
            vo.setChildren(buildTree(childrenMap, perm.getId()));
            result.add(vo);
        }
        return result;
    }

    private PermissionVO toVO(SysPermission perm) {
        PermissionVO vo = new PermissionVO();
        vo.setId(perm.getId());
        vo.setParentId(perm.getParentId());
        vo.setPermCode(perm.getPermCode());
        vo.setPermName(perm.getPermName());
        vo.setPermType(perm.getPermType());
        vo.setPath(perm.getPath());
        vo.setSort(perm.getSort());
        return vo;
    }
}
