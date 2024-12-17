package com.study.collect.core.collector;

import com.study.collect.common.enums.collect.CollectType;
import com.study.collect.core.engine.CollectContext;

/**
 * 采集器接口
 */
public interface Collector {
    /**
     * 执行采集
     */
    Object collect(CollectContext context);

    /**
     * 获取采集器类型
     */
    CollectType getType();

    /**
     * 是否支持采集类型
     */
    boolean supports(CollectType type);
}

