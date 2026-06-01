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
 * Gemini provider implementation (Google AI API)
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "flowx.ai.gemini", name = "api-key")
public class GeminiProvider implements AIProvider {

    private final AiProviderConfig.ProviderDetail config;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public GeminiProvider(AiProviderConfig aiProviderConfig, ObjectMapper objectMapper) {
        this.config = aiProviderConfig.getGemini();
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .baseUrl(config.getBaseUrl() != null ? config.getBaseUrl() : "https://generativelanguage.googleapis.com")
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

            ArrayNode contents = requestBody.putArray("contents");

            // Add context messages
            if (context != null) {
                for (Map<String, String> msg : context) {
                    ObjectNode content = contents.addObject();
                    content.put("role", msg.getOrDefault("role", "user").equals("assistant") ? "model" : "user");
                    ArrayNode parts = content.putArray("parts");
                    ObjectNode part = parts.addObject();
                    part.put("text", msg.getOrDefault("content", ""));
                }
            }

            // Add current user message
            ObjectNode userContent = contents.addObject();
            userContent.put("role", "user");
            ArrayNode userParts = userContent.putArray("parts");
            ObjectNode userPart = userParts.addObject();
            userPart.put("text", prompt);

            String model = config.getModel() != null ? config.getModel() : "gemini-2.0-flash";
            String uri = "/v1beta/models/" + model + ":generateContent?key=" + config.getApiKey();

            String responseStr = webClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode responseJson = objectMapper.readTree(responseStr);
            JsonNode candidates = responseJson.get("candidates");
            if (candidates != null && candidates.isArray() && !candidates.isEmpty()) {
                JsonNode parts = candidates.get(0).get("content").get("parts");
                if (parts != null && parts.isArray() && !parts.isEmpty()) {
                    return parts.get(0).get("text").asText();
                }
            }

            throw new BizException(ResultCodeEnum.AI_SERVICE_ERROR.getCode(), "Gemini返回结果为空");
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("Gemini API call failed", e);
            throw new BizException(ResultCodeEnum.AI_SERVICE_ERROR.getCode(), "Gemini服务调用失败: " + e.getMessage());
        }
    }

    @Override
    public String getProviderName() {
        return "gemini";
    }
}
