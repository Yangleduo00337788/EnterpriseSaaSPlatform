package com.flowx.user.mapper;

import com.flowx.user.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * System menu mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * Select menus assigned to a role
     *
     * @param roleId role ID
     * @return list of menus
     */
    List<SysMenu> selectMenusByRoleId(@Param("roleId") Long roleId);

    /**
     * Select all menus ordered for tree building
     *
     * @return list of all menus sorted by parent_id, sort
     */
    List<SysMenu> selectMenuTree();
}
