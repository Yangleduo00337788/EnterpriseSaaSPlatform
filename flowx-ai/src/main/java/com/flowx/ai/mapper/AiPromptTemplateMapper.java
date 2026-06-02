package com.flowx.ai.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.ai.entity.AiPromptTemplate;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI prompt template mapper
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface AiPromptTemplateMapper extends FlexBaseMapper<AiPromptTemplate> {
}
