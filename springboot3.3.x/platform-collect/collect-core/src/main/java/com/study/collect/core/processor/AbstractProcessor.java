package com.study.collect.core.processor;

public abstract class AbstractProcessor<T> implements IProcessor<T> {

    @Override
    public T process(T data) {
        try {
            // 1. 前置处理
            preProcess(data);

            // 2. 执行处理
            T result = doProcess(data);

            // 3. 后置处理
            postProcess(result);

            return result;
        } catch (Exception e) {
            throw new ProcessException("Process failed: " + e.getMessage());
        }
    }

    protected void preProcess(T data) {
        // 默认空实现
    }

    protected abstract T doProcess(T data);

    protected void postProcess(T result) {
        // 默认空实现
    }
}
