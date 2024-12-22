package com.study.collect.core.collector;

public interface ICollector<T, R> {
    /**
     * 执行采集
     */
    R collect(T param);

    /**
     * 获取采集器类型
     */
    String getType();
}
