package com.study.collect.core.collector;

import com.study.collect.core.annotation.Collector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCollector<T, R> implements ICollector<T, R> {

    @Override
    public R collect(T param) {
        try {
            // 1. 前置处理
            preProcess(param);

            // 2. 执行采集
            R result = doCollect(param);

            // 3. 后置处理
            postProcess(result);

            return result;
        } catch (Exception e) {
            log.error("Collect failed", e);
//            throw new CollectException("Collect failed: " + e.getMessage());
            throw new RuntimeException("Collect failed: " + e.getMessage());
        }
    }

    /**
     * 前置处理
     */
    protected void preProcess(T param) {
        // 默认空实现
    }

    /**
     * 执行采集
     */
    protected abstract R doCollect(T param);

    /**
     * 后置处理
     */
    protected void postProcess(R result) {
        // 默认空实现
    }
}