package com.flowx.system.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.system.entity.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Operation log mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysOperationLogMapper extends FlexBaseMapper<SysOperationLog> {
}