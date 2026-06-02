package com.flowx.system.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.system.entity.SysTenantPackage;
import org.apache.ibatis.annotations.Mapper;

/**
 * Tenant package mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysTenantPackageMapper extends FlexBaseMapper<SysTenantPackage> {
}