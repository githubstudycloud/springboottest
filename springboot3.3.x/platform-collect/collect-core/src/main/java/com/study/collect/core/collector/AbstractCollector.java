package com.study.collect.core.collector;

import com.study.collect.core.collector.exception.CollectException;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.collector.model.CollectResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCollector<T, R> implements ICollector<T, R> {

    @Override
    public CollectResult<R> collect(CollectContext<T> context) {
        String taskId = context.getTaskId();
        log.info("开始执行采集任务: taskId={}, type={}", taskId, getType());

        try {
            // 前置处理
            preProcess(context);

            // 执行采集
//            CollectResult<R> data = doCollect(context);
            R sourceData = doCollect(context);
            CollectResult<R> data = CollectResult.success(sourceData);
            // 后置处理
            postProcess(data);

            log.info("采集任务执行完成: taskId={}", taskId);
            return CollectResult.success(data.getData());

        } catch (Exception e) {
            log.error("采集任务执行失败: taskId={}", taskId, e);
            return CollectResult.failure(e.getMessage());
        }
    }

    /**
     * 前置处理
     */
    protected void preProcess(CollectContext<T> context) {
        // 数据校验
        validateContext(context);
        // 准备采集参数
        prepareCollectParams(context);
    }

    /**
     * 执行采集
     */
//    protected abstract CollectResult<R> doCollect(CollectContext<T> context);
    protected abstract R doCollect(CollectContext<T> context);

    /**
     * 后置处理
     */
    protected void postProcess(CollectResult<R> data) {
        // 数据清洗
        cleanCollectData(data);
        // 结果验证
        validateCollectResult(data);
    }

    /**
     * 上下文校验
     */
    protected void validateContext(CollectContext<T> context) {
        if (context == null) {
            throw new CollectException("采集上下文不能为空");
        }
        if (context.getTaskId() == null) {
            throw new CollectException("任务ID不能为空");
        }
        if (context.getParams() == null) {
            throw new CollectException("采集参数不能为空");
        }
    }

    /**
     * 准备采集参数
     */
    protected void prepareCollectParams(CollectContext<T> context) {
        // 子类可覆盖实现具体的参数准备逻辑
    }

    /**
     * 清洗采集数据
     */
    protected void cleanCollectData(CollectResult<R> data) {
        // 子类可覆盖实现具体的数据清洗逻辑
    }

    /**
     * 验证采集结果
     */
    protected void validateCollectResult(CollectResult<R> data) {
        // 子类可覆盖实现具体的结果验证逻辑
    }
}