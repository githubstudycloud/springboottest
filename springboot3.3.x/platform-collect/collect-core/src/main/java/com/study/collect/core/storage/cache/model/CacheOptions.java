package com.study.collect.core.storage.cache.model;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
@Builder
public class CacheOptions {
    // 过期时间
    private long expiration;

    // 时间单位
    private TimeUnit timeUnit;

    // 是否允许空值缓存
    private boolean cacheNull;

    // 是否使用压缩
    private boolean useCompression;

    // 自定义序列化器
    private String serializer;

    public static CacheOptions defaultOptions() {
        return CacheOptions.builder()
                .expiration(3600)
                .timeUnit(TimeUnit.SECONDS)
                .cacheNull(false)
                .useCompression(false)
                .build();
    }
}