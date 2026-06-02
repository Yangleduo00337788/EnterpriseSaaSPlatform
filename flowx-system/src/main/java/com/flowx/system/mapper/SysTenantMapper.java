package com.flowx.system.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.system.entity.SysTenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * Tenant mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysTenantMapper extends FlexBaseMapper<SysTenant> {
}