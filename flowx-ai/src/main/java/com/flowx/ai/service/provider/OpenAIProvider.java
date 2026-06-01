package com.flowx.ai.service.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flowx.ai.config.AiProviderConfig;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OpenAI provider implementation
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "flowx.ai.openai", name = "api-key")
public class OpenAIProvider implements AIProvider {

    private final AiProviderConfig.ProviderDetail config;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public OpenAIProvider(AiProviderConfig aiProviderConfig, ObjectMapper objectMapper) {
        this.config = aiProviderConfig.getOpenai();
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .baseUrl(config.getBaseUrl() != null ? config.getBaseUrl() : "https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + config.getApiKey())
                .build();
    }

    @Override
    public String chat(String prompt) {
        return chat(prompt, new ArrayList<>());
    }

    @Override
    public String chat(String prompt, List<Map<String, String>> context) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", config.getModel() != null ? config.getModel() : "gpt-4o");
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2048);

            ArrayNode messages = requestBody.putArray("messages");

            // Add context messages
            if (context != null) {
                for (Map<String, String> msg : context) {
                    ObjectNode message = messages.addObject();
                    message.put("role", msg.getOrDefault("role", "user"));
                    message.put("content", msg.getOrDefault("content", ""));
                }
            }

            // Add current user message
            ObjectNode userMessage = messages.addObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            String responseStr = webClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode responseJson = objectMapper.readTree(responseStr);
            JsonNode choices = responseJson.get("choices");
            if (choices != null && choices.isArray() && !choices.isEmpty()) {
                return choices.get(0).get("message").get("content").asText();
            }

            throw new BizException(ResultCodeEnum.AI_SERVICE_ERROR.getCode(), "OpenAI返回结果为空");
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("OpenAI API call failed", e);
            throw new BizException(ResultCodeEnum.AI_SERVICE_ERROR.getCode(), "OpenAI服务调用失败: " + e.getMessage());
        }
    }

    @Override
    public String getProviderName() {
        return "openai";
    }
}
