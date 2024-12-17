package com.study.collect.common.exception.collect;

// 采集异常

import com.study.collect.common.exception.system.BusinessException;

/**
 * 采集异常 - 用于数据采集过程中的错误
 */
public class CollectException extends BusinessException {

    public CollectException(String code, String message) {
        super(code, message);
    }

    public CollectException(String message) {
        super("COLLECT_ERROR", message);
    }

    public CollectException(String message, Throwable cause) {
        super("COLLECT_ERROR", message, cause);
    }
}
