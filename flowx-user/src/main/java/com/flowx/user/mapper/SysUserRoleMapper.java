package com.flowx.user.mapper;

import com.flowx.user.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * User-role association mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
