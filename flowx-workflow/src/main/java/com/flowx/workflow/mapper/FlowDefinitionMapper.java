package com.flowx.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowx.workflow.entity.FlowDefinition;
import org.apache.ibatis.annotations.Mapper;

/**
 * Flow definition mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface FlowDefinitionMapper extends BaseMapper<FlowDefinition> {
}
