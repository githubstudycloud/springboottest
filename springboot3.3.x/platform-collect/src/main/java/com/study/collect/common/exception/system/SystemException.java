package com.study.collect.common.exception.system;

// 系统异常

import com.study.collect.common.exception.BaseException;

/**
 * 系统异常 - 用于系统级别的异常
 */
public class SystemException extends BaseException {

    public SystemException(String code, String message) {
        super(code, message);
    }

    public SystemException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}