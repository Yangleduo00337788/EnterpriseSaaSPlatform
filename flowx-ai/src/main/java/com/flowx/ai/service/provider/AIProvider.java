package com.flowx.ai.service.provider;

import java.util.List;
import java.util.Map;

/**
 * AI provider interface
 *
 * @author FlowX
 * @since 1.0.0
 */
public interface AIProvider {

    /**
     * Send a single prompt and get response
     *
     * @param prompt user prompt
     * @return AI response text
     */
    String chat(String prompt);

    /**
     * Send a prompt with conversation context and get response
     *
     * @param prompt  user prompt
     * @param context conversation context (list of role/content maps)
     * @return AI response text
     */
    String chat(String prompt, List<Map<String, String>> context);

    /**
     * Get the provider name
     *
     * @return provider name (e.g., "openai", "deepseek")
     */
    String getProviderName();
}
