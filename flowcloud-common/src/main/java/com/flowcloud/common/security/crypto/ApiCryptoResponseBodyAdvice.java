package com.flowcloud.common.security.crypto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.crypto.SecretKey;

@RestControllerAdvice(annotations = {RestController.class, Controller.class})
@RequiredArgsConstructor
public class ApiCryptoResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ApiCryptoService apiCryptoService;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType)
                && !shouldSkip(returnType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return body;
        }
        HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
        if (!apiCryptoService.shouldProcess(httpServletRequest)
                || body instanceof ApiCryptoEnvelope
                || Boolean.TRUE.equals(httpServletRequest.getAttribute(ApiCryptoConstants.SKIP_RESPONSE_ENCRYPTION_ATTRIBUTE))) {
            return body;
        }
        SecretKey secretKey;
        try {
            secretKey = apiCryptoService.resolveRequestKey(httpServletRequest);
        } catch (IllegalArgumentException ex) {
            return body;
        }
        if (secretKey == null) {
            return body;
        }
        return apiCryptoService.encryptResponseBody(body, secretKey);
    }

    private boolean shouldSkip(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(SkipApiEncryption.class)
                || returnType.getContainingClass().isAnnotationPresent(SkipApiEncryption.class);
    }
}
