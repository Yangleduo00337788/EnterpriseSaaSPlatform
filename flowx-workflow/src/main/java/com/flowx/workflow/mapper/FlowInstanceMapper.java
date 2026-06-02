package com.flowx.workflow.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.workflow.entity.FlowInstance;
import org.apache.ibatis.annotations.Mapper;

/**
 * Flow instance mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface FlowInstanceMapper extends FlexBaseMapper<FlowInstance> {
}
