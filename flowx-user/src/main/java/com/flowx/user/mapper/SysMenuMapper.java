package com.flowx.user.mapper;

import com.flowx.user.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.flowx.infrastructure.persistence.FlexBaseMapper;

import java.util.List;

@Mapper
public interface SysMenuMapper extends FlexBaseMapper<SysMenu> {

    List<SysMenu> selectMenusByRoleId(@Param("roleId") Long roleId);

    List<SysMenu> selectMenuTree();

    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);
}