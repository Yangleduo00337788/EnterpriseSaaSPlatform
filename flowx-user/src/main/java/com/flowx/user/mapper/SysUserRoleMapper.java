package com.flowx.user.mapper;

import com.flowx.user.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import com.flowx.infrastructure.persistence.FlexBaseMapper;

/**
 * User-role association mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysUserRoleMapper extends FlexBaseMapper<SysUserRole> {
}
