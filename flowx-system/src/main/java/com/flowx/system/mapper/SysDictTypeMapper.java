package com.flowx.system.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.system.entity.SysDictType;
import org.apache.ibatis.annotations.Mapper;

/**
 * Dictionary type mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysDictTypeMapper extends FlexBaseMapper<SysDictType> {
}