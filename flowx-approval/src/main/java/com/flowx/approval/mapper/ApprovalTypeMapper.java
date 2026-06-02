package com.flowx.approval.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.approval.entity.ApprovalType;
import org.apache.ibatis.annotations.Mapper;

/**
 * Approval type mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface ApprovalTypeMapper extends FlexBaseMapper<ApprovalType> {
}
