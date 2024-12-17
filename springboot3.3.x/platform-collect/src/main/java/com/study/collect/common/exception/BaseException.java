package com.study.collect.common.exception;

import lombok.Getter;

/**
 * 基础异常类,所有业务异常的父类
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private final String code;
    private final String message;

    protected BaseException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    protected BaseException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}