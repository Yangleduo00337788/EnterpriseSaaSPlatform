package com.flowx.system.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.system.entity.SysDictData;
import org.apache.ibatis.annotations.Mapper;

/**
 * Dictionary data mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface SysDictDataMapper extends FlexBaseMapper<SysDictData> {
}