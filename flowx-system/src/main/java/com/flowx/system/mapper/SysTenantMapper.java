package com.flowx.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowx.system.entity.SysTenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * Tenant mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysTenantMapper extends BaseMapper<SysTenant> {
}
