package com.study.collect.common.exception.sync;

// 同步异常

import com.study.collect.common.exception.system.BusinessException;

/**
 * 同步异常 - 用于数据同步过程中的错误
 */
public class SyncException extends BusinessException {

    public SyncException(String code, String message) {
        super(code, message);
    }

    public SyncException(String message) {
        super("SYNC_ERROR", message);
    }

    public SyncException(String message, Throwable cause) {
        super("SYNC_ERROR", message, cause);
    }
}