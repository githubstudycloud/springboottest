package com.study.collect.core.strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 容错链
 */
@Slf4j
public class FaultToleranceChain<T> {

    private CircuitBreaker circuitBreaker;
    private Retry retry;
    private Bulkhead bulkhead;
    private Fallback<T> fallback;
    private Supplier<T> operation;

    /**
     * 执行容错链
     */
    public T execute() {
        try {
            // 1. 检查熔断器状态
            if (circuitBreaker != null &&
                    !circuitBreaker.isCallPermitted()) {
                return executeFallback(
                        new CircuitBreakerOpenException());
            }

            // 2. 执行带重试的操作
            return executeWithRetry();

        } catch (Exception e) {
            // 3. 处理异常
            return handleExecutionException(e);
        }
    }

    /**
     * 执行带重试的操作
     */
    private T executeWithRetry() throws Exception {
        if (retry != null) {
            return retry.executeSupplier(() ->
                    executeWithBulkhead());
        }
        return executeWithBulkhead();
    }

    /**
     * 执行带隔板的操作
     */
    private T executeWithBulkhead() throws Exception {
        if (bulkhead != null) {
            return bulkhead.executeSupplier(() ->
                    executeOperation());
        }
        return executeOperation();
    }

    /**
     * 执行实际操作
     */
    private T executeOperation() {
        try {
            T result = operation.get();
            if (circuitBreaker != null) {
                circuitBreaker.onSuccess();
            }
            return result;
        } catch (Exception e) {
            if (circuitBreaker != null) {
                circuitBreaker.onError();
            }
            throw e;
        }
    }

    /**
     * 执行降级策略
     */
    private T executeFallback(Exception e) {
        if (fallback != null) {
            return fallback.execute(e);
        }
        throw new FaultToleranceException(
                "No fallback available", e);
    }

    /**
     * 处理执行异常
     */
    private T handleExecutionException(Exception e) {
        log.error("Execution failed in fault tolerance chain", e);
        return executeFallback(e);
    }
}