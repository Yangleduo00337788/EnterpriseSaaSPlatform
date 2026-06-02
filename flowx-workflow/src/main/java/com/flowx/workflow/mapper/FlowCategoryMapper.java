package com.flowx.workflow.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.workflow.entity.FlowCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * Flow category mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface FlowCategoryMapper extends FlexBaseMapper<FlowCategory> {
}
