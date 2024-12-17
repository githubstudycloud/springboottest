package com.study.collect.common.constant.cache;

// 缓存常量
/**
 * 缓存相关常量
 */
public final class CacheConstant {
    private CacheConstant() {}

    public static final String CACHE_PREFIX = "COLLECT_";
    public static final long DEFAULT_EXPIRE_TIME = 3600L;
    public static final String DATA_CACHE_PREFIX = CACHE_PREFIX + "DATA_";
    public static final String TASK_CACHE_PREFIX = CACHE_PREFIX + "TASK_";
}