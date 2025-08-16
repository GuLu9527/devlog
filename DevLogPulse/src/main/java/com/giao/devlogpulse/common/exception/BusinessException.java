package com.giao.devlogpulse.common.exception;

import com.giao.devlogpulse.common.api.IErrorCode;
import lombok.Getter;

/**
 * 自定义业务异常
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final IErrorCode errorCode;
    
    public BusinessException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public BusinessException(IErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BusinessException(String message) {
        super(message);
        this.errorCode = null;
    }
}