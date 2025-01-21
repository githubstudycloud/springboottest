package com.study.collect.core.storage.cache.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.core.storage.cache.config.CacheProperties;
import com.study.collect.core.storage.cache.model.CacheOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Redis缓存管理器实现
 * 提供基于Redis的缓存操作实现，支持数据自动加载、类型转换和批量操作
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheManager implements CacheManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;

    @Override
    public <T> T get(String key, Class<T> type, Supplier<T> supplier) {
        return get(key, type, supplier, null);
    }

    @Override
    public <T> T get(String key, Class<T> type, Supplier<T> supplier, CacheOptions options) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return convertValue(value, type);
        }

        T newValue = supplier.get();
        if (newValue != null) {
            put(key, newValue, options);
        }
        return newValue;
    }

    @Override
    public <T> Collection<T> multiGet(Collection<String> keys, Class<T> type, Supplier<Collection<T>> supplier) {
        return multiGet(keys, type, supplier, null);
    }

    @Override
    public <T> Collection<T> multiGet(Collection<String> keys, Class<T> type,
                                      Supplier<Collection<T>> supplier, CacheOptions options) {
        List<Object> values = Optional.ofNullable(redisTemplate.opsForValue().multiGet(keys))
                .orElse(List.of());

        if (!values.isEmpty() && !values.contains(null)) {
            return values.stream()
                    .map(value -> convertValue(value, type))
                    .collect(Collectors.toList());
        }

        Collection<T> newValues = supplier.get();
        if (newValues != null && !newValues.isEmpty()) {
            newValues.forEach(value -> put(generateKey(value), value, options));
        }
        return newValues;
    }

    @Override
    public <T> void put(String key, T value) {
        put(key, value, null);
    }

    @Override
    public <T> void put(String key, T value, CacheOptions options) {
        CacheOptions actualOptions = Optional.ofNullable(options)
                .orElse(CacheOptions.defaultOptions());

        redisTemplate.opsForValue().set(
                key,
                value,
                actualOptions.getExpiration(),
                actualOptions.getTimeUnit()
        );
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void removeByPrefix(String prefix) {
        Optional.ofNullable(redisTemplate.keys(prefix + "*"))
                .filter(keys -> !keys.isEmpty())
                .ifPresent(redisTemplate::delete);
    }

    /**
     * 将缓存值转换为指定类型
     */
    @SuppressWarnings("unchecked")
    private <T> T convertValue(Object value, Class<T> type) {
        try {
            if (type.isInstance(value)) {
                return (T) value;
            }
            return objectMapper.convertValue(value, type);
        } catch (Exception e) {
            log.error("缓存值类型转换失败: value={}, targetType={}", value, type, e);
            return null;
        }
    }

    /**
     * 生成缓存键
     */
    private <T> String generateKey(T value) {
        return String.format("%s:%d", value.getClass().getSimpleName(), value.hashCode());
    }
}