package com.study.collect.core.storage.cache.manager;

import com.study.collect.core.storage.cache.model.CacheOptions;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * 缓存管理器接口
 * 提供统一的缓存操作能力，支持自动加载、批量操作和类型安全的数据访问
 */
public interface CacheManager {

    /**
     * 获取缓存，如果不存在则通过supplier加载并缓存
     *
     * @param key 缓存键
     * @param type 返回值类型
     * @param supplier 数据加载器
     * @return 缓存的值
     * @param <T> 值类型
     */
    <T> T get(String key, Class<T> type, Supplier<T> supplier);

    /**
     * 使用自定义选项获取缓存
     *
     * @param key 缓存键
     * @param type 返回值类型
     * @param supplier 数据加载器
     * @param options 缓存选项
     * @return 缓存的值
     * @param <T> 值类型
     */
    <T> T get(String key, Class<T> type, Supplier<T> supplier, CacheOptions options);

    /**
     * 批量获取缓存，如果不存在则通过supplier加载并缓存
     *
     * @param keys 缓存键集合
     * @param type 返回值类型
     * @param supplier 数据加载器
     * @return 缓存值集合
     * @param <T> 值类型
     */
    <T> Collection<T> multiGet(Collection<String> keys, Class<T> type, Supplier<Collection<T>> supplier);

    /**
     * 使用自定义选项批量获取缓存
     *
     * @param keys 缓存键集合
     * @param type 返回值类型
     * @param supplier 数据加载器
     * @param options 缓存选项
     * @return 缓存值集合
     * @param <T> 值类型
     */
    <T> Collection<T> multiGet(Collection<String> keys, Class<T> type, Supplier<Collection<T>> supplier, CacheOptions options);

    /**
     * 更新缓存
     *
     * @param key 缓存键
     * @param value 缓存值
     * @param <T> 值类型
     */
    <T> void put(String key, T value);

    /**
     * 使用自定义选项更新缓存
     *
     * @param key 缓存键
     * @param value 缓存值
     * @param options 缓存选项
     * @param <T> 值类型
     */
    <T> void put(String key, T value, CacheOptions options);

    /**
     * 删除指定键的缓存
     *
     * @param key 缓存键
     */
    void remove(String key);

    /**
     * 删除指定前缀的所有缓存
     *
     * @param prefix 缓存键前缀
     */
    void removeByPrefix(String prefix);
}