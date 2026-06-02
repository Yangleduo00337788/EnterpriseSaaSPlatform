package com.flowx.system.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.system.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * System config mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysConfigMapper extends FlexBaseMapper<SysConfig> {
}