package com.study.collect.core.cache;

import com.study.collect.core.cache.manager.CacheManager;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheMetrics {

    private final CacheManager cacheManager;
    private Counter cacheHits;
    private Counter cacheMisses;
    private Counter cacheEvictions;

    @PostConstruct
    public void init() {
        // 注册Prometheus指标
        cacheHits = Counter.builder("cache_hits_total")
                .description("Cache hits total")
                .register(Metrics.globalRegistry);

        cacheMisses = Counter.builder("cache_misses_total")
                .description("Cache misses total")
                .register(Metrics.globalRegistry);

        cacheEvictions = Counter.builder("cache_evictions_total")
                .description("Cache evictions total")
                .register(Metrics.globalRegistry);
    }

    public void recordCacheHit() {
        cacheHits.increment();
    }

    public void recordCacheMiss() {
        cacheMisses.increment();
    }

    public void recordCacheEviction() {
        cacheEvictions.increment();
    }
}