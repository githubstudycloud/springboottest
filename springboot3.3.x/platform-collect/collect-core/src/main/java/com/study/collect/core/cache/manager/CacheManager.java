package com.study.collect.core.cache.manager;

import java.util.concurrent.TimeUnit;

// 3. CacheManager接口
public interface CacheManager {
    /**
     * 设置缓存
     */
    <T> void set(String key, T value, long expire, TimeUnit timeUnit);

    /**
     * 获取缓存
     */
    <T> T get(String key, Class<T> type);

    /**
     * 删除缓存
     */
    void delete(String key);

    /**
     * 清除前缀
     */
    void deleteByPrefix(String prefix);
}