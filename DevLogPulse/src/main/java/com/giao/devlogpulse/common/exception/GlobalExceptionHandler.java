package com.giao.devlogpulse.common.exception;

import com.giao.devlogpulse.common.api.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Object>> handleBusinessException(BusinessException e) {
        log.error("🚫 Business异常: {} - ErrorCode: {}", e.getMessage(), e.getErrorCode());
        
        if (e.getErrorCode() != null) {
            Integer code = e.getErrorCode().getCode();
            Result<Object> result = Result.failed(e.getErrorCode());
            
            log.error("📊 异常详情 - Code: {}, Message: {}", code, result.getMessage());
            
            // 根据错误码返回对应的HTTP状态码
            if (code == 401) {
                log.error("🔐 返回401 UNAUTHORIZED");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            } else if (code == 403) {
                log.error("🚫 返回403 FORBIDDEN");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
            } else if (code == 404) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            } else if (code == 500) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.ok(Result.failed(e.getMessage()));
        }
    }

    /**
     * 处理参数校验异常（@RequestBody）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            errorMsg.append(fieldError.getDefaultMessage()).append("; ");
        }
        log.warn("参数校验异常: {}", errorMsg.toString());
        return Result.validateFailed(errorMsg.toString());
    }

    /**
     * 处理参数绑定异常（@ModelAttribute）
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleBindException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            errorMsg.append(fieldError.getDefaultMessage()).append("; ");
        }
        log.warn("参数绑定异常: {}", errorMsg.toString());
        return Result.validateFailed(errorMsg.toString());
    }

    /**
     * 处理IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数异常: {}", e.getMessage());
        return Result.validateFailed(e.getMessage());
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.failed("系统繁忙，请稍后重试");
    }
}