package com.flowx.workflow.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.workflow.entity.FlowTaskLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Flow task log mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface FlowTaskLogMapper extends FlexBaseMapper<FlowTaskLog> {
}
