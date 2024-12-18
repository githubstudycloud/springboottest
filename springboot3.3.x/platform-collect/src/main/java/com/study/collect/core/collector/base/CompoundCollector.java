package com.study.collect.core.collector.base;

// 复合采集基类
import com.study.collect.common.enums.collect.CollectType;
import com.study.collect.common.exception.collect.CollectException;
import com.study.collect.core.engine.CollectContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 复合采集器基类
 * 用于组合多个采集器实现复杂采集逻辑
 */
@Slf4j
public abstract class CompoundCollector extends AbstractCollector {

    @Override
    public CollectType getType() {
        return CollectType.COMPOUND;
    }

    @Override
    public boolean supports(CollectType type) {
        return CollectType.COMPOUND.equals(type);
    }

    @Override
    protected Object doCollect(CollectContext context) {
        try {
            // 1. 准备采集器
            initCollectors(context);

            // 2. 主采集过程
            Object primaryData = doPrimaryCollect(context);

            // 3. 详细数据采集
            Object detailData = doDetailCollect(context, primaryData);

            // 4. 组合数据
            return combine(primaryData, detailData);

        } catch (Exception e) {
            log.error("Compound collect failed", e);
            throw new CollectException("Compound collect failed: " + e.getMessage());
        }
    }

    /**
     * 初始化采集器
     */
    protected abstract void initCollectors(CollectContext context);

    /**
     * 执行主采集
     */
    protected abstract Object doPrimaryCollect(CollectContext context);

    /**
     * 执行详细数据采集
     */
    protected abstract Object doDetailCollect(CollectContext context, Object primaryData);

    /**
     * 组合采集结果
     */
    protected abstract Object combine(Object primaryData, Object detailData);
}
