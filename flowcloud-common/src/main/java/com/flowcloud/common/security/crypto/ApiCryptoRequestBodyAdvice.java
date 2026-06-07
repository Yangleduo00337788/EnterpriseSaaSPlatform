package com.flowcloud.common.security.crypto;

import com.flowcloud.common.exception.BusinessException;
import com.flowcloud.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@RestControllerAdvice(annotations = {RestController.class, Controller.class})
@RequiredArgsConstructor
public class ApiCryptoRequestBodyAdvice implements RequestBodyAdvice {

    private final ApiCryptoService apiCryptoService;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        HttpServletRequest request = currentRequest();
        return request != null
                && apiCryptoService.shouldProcess(request)
                && !shouldSkip(methodParameter);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return inputMessage;
        }
        SecretKey secretKey;
        try {
            secretKey = apiCryptoService.resolveRequestKey(request);
        } catch (IllegalArgumentException ex) {
            request.setAttribute(ApiCryptoConstants.SKIP_RESPONSE_ENCRYPTION_ATTRIBUTE, Boolean.TRUE);
            throw new BusinessException(ResultCode.API_CRYPTO_KEY_EXPIRED.getCode(), "接口加密密钥已失效，请重试");
        }
        if (secretKey == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "接口加密请求缺少密钥");
        }
        String rawBody = StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8);
        if (!org.springframework.util.StringUtils.hasText(rawBody)) {
            return inputMessage;
        }
        String decryptedBody;
        try {
            decryptedBody = apiCryptoService.decryptRequestBody(rawBody, secretKey);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), ex.getMessage());
        }
        return new DecryptedHttpInputMessage(inputMessage.getHeaders(), decryptedBody.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                  Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    private boolean shouldSkip(MethodParameter methodParameter) {
        return methodParameter.hasMethodAnnotation(SkipApiEncryption.class)
                || methodParameter.getContainingClass().isAnnotationPresent(SkipApiEncryption.class);
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    private record DecryptedHttpInputMessage(HttpHeaders headers, byte[] body) implements HttpInputMessage {

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }
    }
}
