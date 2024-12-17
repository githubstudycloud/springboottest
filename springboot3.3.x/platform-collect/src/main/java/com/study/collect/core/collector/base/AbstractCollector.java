package com.study.collect.core.collector.base;

// 采集器基类

import com.study.collect.common.exception.collect.CollectException;
import com.study.collect.core.engine.CollectContext;

/**
 * 抽象采集器基类
 */
@Slf4j
public abstract class AbstractCollector implements Collector {

    @Override
    public Object collect(CollectContext context) {
        try {
            // 前置检查
            preCheck(context);

            // 执行采集
            Object data = doCollect(context);

            // 后置处理
            return postProcess(data, context);
        } catch (Exception e) {
            log.error("Collect failed", e);
            throw new CollectException("Collect failed: " + e.getMessage(), e);
        }
    }

    /**
     * 前置检查
     */
    protected void preCheck(CollectContext context) {
        if (!supports(context.getCollectType())) {
            throw new CollectException("Unsupported collect type: " + context.getCollectType());
        }
    }

    /**
     * 执行采集
     */
    protected abstract Object doCollect(CollectContext context);

    /**
     * 后置处理
     */
    protected Object postProcess(Object data, CollectContext context) {
        return data;
    }
}
