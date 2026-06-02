package com.flowx.system.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.system.entity.SysLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Login log mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysLoginLogMapper extends FlexBaseMapper<SysLoginLog> {
}