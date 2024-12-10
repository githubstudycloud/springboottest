package com.study.collect.enums;

public enum RetryStrategy {
    FIXED_INTERVAL,    // 固定时间间隔重试
    EXPONENTIAL_BACKOFF, // 指数退避重试
    LINEAR_BACKOFF      // 线性退避重试
}