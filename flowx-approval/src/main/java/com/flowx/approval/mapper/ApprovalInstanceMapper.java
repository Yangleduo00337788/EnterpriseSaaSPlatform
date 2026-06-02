package com.flowx.approval.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.approval.entity.ApprovalInstance;
import org.apache.ibatis.annotations.Mapper;

/**
 * Approval instance mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface ApprovalInstanceMapper extends FlexBaseMapper<ApprovalInstance> {
}
