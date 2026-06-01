package com.flowx.admin.config;

import com.flowx.common.core.exception.AuthException;
import com.flowx.common.core.exception.BizException;
import com.flowx.common.core.exception.ForbiddenException;
import com.flowx.common.core.exception.NotFoundException;
import com.flowx.common.core.result.R;
import com.flowx.common.core.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler
 *
 * @author FlowX
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle business exception
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Void> handleBizException(BizException e) {
        log.warn("Business exception: code={}, message={}", e.getCode(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * Handle authentication exception (401)
     */
    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<Void> handleAuthException(AuthException e) {
        log.warn("Authentication exception: {}", e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * Handle forbidden exception (403)
     */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<Void> handleForbiddenException(ForbiddenException e) {
        log.warn("Forbidden exception: {}", e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * Handle not found exception (404)
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<Void> handleNotFoundException(NotFoundException e) {
        log.warn("Not found exception: {}", e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * Handle method argument not valid exception
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("请求参数验证失败");
        log.warn("Validation exception: {}", message);
        return R.fail(ResultCodeEnum.BAD_REQUEST.getCode(), message);
    }

    /**
     * Handle bind exception
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("请求参数绑定失败");
        log.warn("Bind exception: {}", message);
        return R.fail(ResultCodeEnum.BAD_REQUEST.getCode(), message);
    }

    /**
     * Handle HTTP method not supported exception
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public R<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("Method not supported: {}", e.getMessage());
        return R.fail(405, "不支持的请求方法: " + e.getMethod());
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception e) {
        log.error("Unexpected exception", e);
        return R.fail(ResultCodeEnum.INTERNAL_ERROR.getCode(), "服务器内部错误");
    }
}
