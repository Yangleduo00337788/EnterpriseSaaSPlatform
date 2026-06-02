package com.flowx.ai.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.ai.entity.AiConversation;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI conversation mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface AiConversationMapper extends FlexBaseMapper<AiConversation> {
}
