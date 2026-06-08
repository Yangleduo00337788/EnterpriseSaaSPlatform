package com.flowcloud.common.exception;

import com.flowcloud.common.result.Result;
import com.flowcloud.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(Exception e) {
        String message = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException ex && ex.getBindingResult().hasErrors()) {
            message = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        } else if (e instanceof BindException ex && ex.getBindingResult().hasErrors()) {
            message = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        }
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        long maxUploadSize = e.getMaxUploadSize();
        String sizeText = maxUploadSize > 0 ? formatFileSize(maxUploadSize) : "允许范围";
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), "上传文件过大，请控制在 " + sizeText + " 以内");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail("系统繁忙，请稍后重试");
    }

    private String formatFileSize(long size) {
        if (size >= 1024L * 1024L * 1024L) {
            return String.format("%.0fGB", size / 1024d / 1024d / 1024d);
        }
        if (size >= 1024L * 1024L) {
            return String.format("%.0fMB", size / 1024d / 1024d);
        }
        if (size >= 1024L) {
            return String.format("%.0fKB", size / 1024d);
        }
        return size + "B";
    }
}
