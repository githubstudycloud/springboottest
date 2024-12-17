package com.study.collect.common.exception.collect;

// 处理异常

import com.study.collect.common.exception.system.BusinessException;

/**
 * 处理异常 - 用于数据处理过程中的错误
 */
public class ProcessException extends BusinessException {

    public ProcessException(String code, String message) {
        super(code, message);
    }

    public ProcessException(String message) {
        super("PROCESS_ERROR", message);
    }

    public ProcessException(String message, Throwable cause) {
        super("PROCESS_ERROR", message, cause);
    }
}
