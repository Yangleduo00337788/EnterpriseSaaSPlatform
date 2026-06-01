package com.flowx.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowx.ai.entity.AiConversation;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI conversation mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface AiConversationMapper extends BaseMapper<AiConversation> {
}
