package com.flowx.message.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.message.entity.MsgRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * External message record mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface MsgRecordMapper extends FlexBaseMapper<MsgRecord> {
}
