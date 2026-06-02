package com.flowx.user.mapper;

import com.flowx.user.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;
import com.flowx.infrastructure.persistence.FlexBaseMapper;

/**
 * System department mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysDeptMapper extends FlexBaseMapper<SysDept> {
}
