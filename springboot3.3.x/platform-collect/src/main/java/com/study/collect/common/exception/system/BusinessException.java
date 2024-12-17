package com.study.collect.common.exception.system;

import com.study.collect.common.exception.BaseException;

/**
 * 业务异常 - 用于业务逻辑异常
 */
public class BusinessException extends BaseException {

    public BusinessException(String code, String message) {
        super(code, message);
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}