package com.study.collect.core.collector;

import com.study.collect.core.cache.annotation.Cache;
import com.study.collect.core.cache.annotation.CacheLock;

public interface ICollector<T, R> {
    /**
     * 执行采集
     */
    // 采集数据
    @Cache(prefix = "collect")              // 缓存支持
    @CacheLock(prefix = "collect_lock")     // 分布式锁
    R collect(T param);

    /**
     * 获取采集器类型
     */
    String getType();
}
