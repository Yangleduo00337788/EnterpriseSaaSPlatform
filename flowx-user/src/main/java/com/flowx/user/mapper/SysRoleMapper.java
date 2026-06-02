package com.flowx.user.mapper;

import com.flowx.user.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import com.flowx.infrastructure.persistence.FlexBaseMapper;

/**
 * System role mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysRoleMapper extends FlexBaseMapper<SysRole> {
}
