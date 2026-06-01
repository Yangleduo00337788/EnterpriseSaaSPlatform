package com.flowx.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowx.workflow.entity.FlowTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * Flow task mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface FlowTaskMapper extends BaseMapper<FlowTask> {
}
