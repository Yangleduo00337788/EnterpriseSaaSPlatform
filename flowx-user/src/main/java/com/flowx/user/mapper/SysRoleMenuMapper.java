package com.flowx.user.mapper;

import com.flowx.user.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import com.flowx.infrastructure.persistence.FlexBaseMapper;

/**
 * Role-menu association mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysRoleMenuMapper extends FlexBaseMapper<SysRoleMenu> {
}
