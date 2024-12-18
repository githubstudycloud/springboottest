package com.study.collect.core.strategy;

import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MultiLevelCacheManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisCacheWriter cacheWriter;
    private final CaffeineCacheManager localCacheManager;
    private final MetricsCollector metricsCollector;

    // 本地缓存配置
    private final Cache<String, Object> localCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats()
            .build();

    /**
     * 获取缓存数据
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Timer.Sample timer = metricsCollector.startTimer("cache_get");
        try {
            // 1. 从本地缓存获取
            Object localValue = localCache.getIfPresent(key);
            if (localValue != null) {
                metricsCollector.incrementCounter("cache_hit_local");
                return (T) localValue;
            }

            // 2. 从Redis获取
            Object redisValue = redisTemplate.opsForValue().get(key);
            if (redisValue != null) {
                metricsCollector.incrementCounter("cache_hit_redis");
                // 回填本地缓存
                localCache.put(key, redisValue);
                return (T) redisValue;
            }

            // 3. 缓存未命中
            metricsCollector.incrementCounter("cache_miss");
            return null;

        } catch (Exception e) {
            log.error("Cache get failed: {}", key, e);
            throw new CacheException("Cache get failed: " + e.getMessage());
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    /**
     * 设置缓存数据
     */
    public void set(String key, Object value, long expireSeconds) {
        Timer.Sample timer = metricsCollector.startTimer("cache_set");
        try {
            // 1. 设置Redis缓存
            redisTemplate.opsForValue().set(
                    key,
                    value,
                    expireSeconds,
                    TimeUnit.SECONDS
            );

            // 2. 更新本地缓存
            localCache.put(key, value);

        } catch (Exception e) {
            log.error("Cache set failed: {}", key, e);
            throw new CacheException("Cache set failed: " + e.getMessage());
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    /**
     * 缓存预热
     */
    public void warmup(String region) {
        log.info("Starting cache warmup for region: {}", region);
        Timer.Sample timer = metricsCollector.startTimer("cache_warmup");

        try {
            // 1. 获取预热配置
            WarmupConfig config = getWarmupConfig(region);

            // 2. 加载数据
            List<WarmupData> dataList = loadWarmupData(config);

            // 3. 并行预热
            warmupParallel(dataList);

            // 4. 验证预热结果
            verifyWarmup(region);

        } catch (Exception e) {
            log.error("Cache warmup failed: {}", region, e);
            throw new CacheException("Cache warmup failed: " + e.getMessage());
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    /**
     * 并行预热
     */
    private void warmupParallel(List<WarmupData> dataList) {
        CompletableFuture<?>[] futures = dataList.stream()
                .map(data -> CompletableFuture.runAsync(() -> {
                    try {
                        warmupSingle(data);
                    } catch (Exception e) {
                        log.error("Warmup data failed: {}", data.getKey(), e);
                    }
                }))
                .toArray(CompletableFuture[]::new);

        try {
            CompletableFuture.allOf(futures).get(5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Parallel warmup failed", e);
            throw new CacheException("Parallel warmup failed", e);
        }
    }

    /**
     * 缓存失效
     */
    public void invalidate(String key, InvalidateOptions options) {
        log.info("Invalidating cache: {}", key);
        Timer.Sample timer = metricsCollector.startTimer("cache_invalidate");

        try {
            // 1. 清除本地缓存
            localCache.invalidate(key);

            // 2. 清除Redis缓存
            redisTemplate.delete(key);

            // 3. 广播失效消息
            if (options.isBroadcast()) {
                broadcastInvalidation(key);
            }

            // 4. 处理级联失效
            if (options.isCascade()) {
                invalidateCascade(key);
            }

        } catch (Exception e) {
            log.error("Cache invalidate failed: {}", key, e);
            throw new CacheException("Cache invalidate failed: " + e.getMessage());
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    /**
     * 处理缓存失效事件
     */
    @EventListener(CacheInvalidateEvent.class)
    public void handleInvalidation(CacheInvalidateEvent event) {
        try {
            // 1. 验证事件
            if (!isValidInvalidateEvent(event)) {
                return;
            }

            // 2. 清除本地缓存
            localCache.invalidate(event.getKey());

            // 3. 记录失效日志
            logInvalidation(event);

        } catch (Exception e) {
            log.error("Handle invalidation failed", e);
        }
    }

    /**
     * 清理过期缓存
     */
    @Scheduled(fixedDelay = 300000) // 5分钟
    public void cleanup() {
        log.info("Starting cache cleanup");
        Timer.Sample timer = metricsCollector.startTimer("cache_cleanup");

        try {
            // 1. 清理本地缓存
            localCache.cleanUp();

            // 2. 清理Redis过期键
            cleanupRedis();

            // 3. 更新统计信息
            updateCacheStats();

        } catch (Exception e) {
            log.error("Cache cleanup failed", e);
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }
}
