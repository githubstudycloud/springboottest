package com.study.collect.core.collector;

import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.collector.model.CollectResult;

// 采集器接口
public interface ICollector<T, R> {
    /**
     * 执行采集
     */
    CollectResult<R> collect(CollectContext<T> context);

    /**
     * 获取采集器类型
     */
    String getType();


}
