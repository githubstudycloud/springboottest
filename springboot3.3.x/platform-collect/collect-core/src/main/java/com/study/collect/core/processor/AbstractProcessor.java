package com.study.collect.core.processor;

// 抽象处理器

import com.study.collect.core.processor.exception.ProcessException;
import com.study.collect.core.processor.model.ProcessContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

@Slf4j
public abstract class AbstractProcessor<T> implements IProcessor<T> {

    @Override
    public T process(T data, ProcessContext context) {
        String taskId = context.getTaskId();
        log.info("开始数据处理: taskId={}, type={}", taskId, getType());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            // 前置处理
            preProcess(data, context);

            // 执行处理
            T result = doProcess(data, context);

            // 后置处理
            result = postProcess(result, context);

            stopWatch.stop();
            log.info("数据处理完成: taskId={}, cost={}ms", taskId, stopWatch.getTotalTimeMillis());

            return result;

        } catch (Exception e) {
            log.error("数据处理异常: taskId={}", taskId, e);
            throw new ProcessException("数据处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 前置处理
     */
    protected void preProcess(T data, ProcessContext context) {
        // 数据校验
        validateData(data);
        // 上下文校验
        validateContext(context);
    }

    /**
     * 执行处理
     */
    protected abstract T doProcess(T data, ProcessContext context);

    /**
     * 后置处理
     */
    protected T postProcess(T result, ProcessContext context) {
        return result;
    }

    /**
     * 数据校验
     */
    protected void validateData(T data) {
        if (data == null) {
            throw new ProcessException("处理数据不能为空");
        }
    }

    /**
     * 上下文校验
     */
    protected void validateContext(ProcessContext context) {
        if (context == null) {
            throw new ProcessException("处理上下文不能为空");
        }
        if (context.getTaskId() == null) {
            throw new ProcessException("任务ID不能为空");
        }
    }
}