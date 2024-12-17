package com.study.collect.common.exception.system;

// 配置异常
/**
 * 配置异常 - 用于配置错误
 */
public class ConfigException extends SystemException {

    public ConfigException(String message) {
        super("CONFIG_ERROR", message);
    }

    public ConfigException(String message, Throwable cause) {
        super("CONFIG_ERROR", message, cause);
    }
}
