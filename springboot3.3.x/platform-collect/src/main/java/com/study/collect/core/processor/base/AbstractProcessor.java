package com.study.collect.core.processor.base;

// 处理器基类

import com.study.collect.common.exception.collect.ProcessException;
import com.study.collect.core.processor.ProcessContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象处理器基类
 */
@Slf4j
public abstract class AbstractProcessor implements Processor {

    @Override
    public void process(ProcessContext context) {
        try {
            // 前置处理
            preProcess(context);

            // 执行处理
            if (shouldProcess(context)) {
                doProcess(context);
            }

            // 后置处理
            postProcess(context);
        } catch (Exception e) {
            log.error("Process failed", e);
            handleError(context, e);
        }
    }

    /**
     * 前置处理
     */
    protected void preProcess(ProcessContext context) {
        // 默认实现为空
    }

    /**
     * 判断是否需要处理
     */
    protected boolean shouldProcess(ProcessContext context) {
        return true;
    }

    /**
     * 执行处理
     */
    protected abstract void doProcess(ProcessContext context);

    /**
     * 后置处理
     */
    protected void postProcess(ProcessContext context) {
        // 默认实现为空
    }

    /**
     * 错误处理
     */
    protected void handleError(ProcessContext context, Exception e) {
        throw new ProcessException("Process failed: " + e.getMessage(), e);
    }
}