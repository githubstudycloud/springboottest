package com.study.collect.common.exception.data;

// 校验异常

import com.study.collect.common.exception.system.BusinessException;

/**
 * 验证异常 - 用于数据验证错误
 */
public class ValidateException extends BusinessException {

    public ValidateException(String code, String message) {
        super(code, message);
    }

    public ValidateException(String message) {
        super("VALIDATE_ERROR", message);
    }

    public ValidateException(String message, Throwable cause) {
        super("VALIDATE_ERROR", message, cause);
    }
}