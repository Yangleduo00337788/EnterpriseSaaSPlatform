package com.flowx.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowx.workflow.entity.FlowTaskLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Flow task log mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface FlowTaskLogMapper extends BaseMapper<FlowTaskLog> {
}
