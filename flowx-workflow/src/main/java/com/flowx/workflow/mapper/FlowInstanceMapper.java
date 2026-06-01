package com.flowx.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowx.workflow.entity.FlowInstance;
import org.apache.ibatis.annotations.Mapper;

/**
 * Flow instance mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface FlowInstanceMapper extends BaseMapper<FlowInstance> {
}
