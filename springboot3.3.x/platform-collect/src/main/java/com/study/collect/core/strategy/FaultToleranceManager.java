package com.study.collect.core.strategy;

import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 系统容错管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FaultToleranceManager {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final BulkheadRegistry bulkheadRegistry;
    private final FallbackRegistry fallbackRegistry;
    private final MetricsCollector metricsCollector;

    /**
     * 执行带容错的操作
     */
    public <T> T executeWithFaultTolerance(
            String operationName,
            Supplier<T> operation,
            FaultToleranceConfig config) {

        Timer.Sample timer = metricsCollector.startTimer(
                "fault_tolerance_execution");

        try {
            // 1. 创建容错链
            FaultToleranceChain<T> chain = buildFaultToleranceChain(
                    operationName,
                    operation,
                    config
            );

            // 2. 执行操作
            return chain.execute();

        } catch (Exception e) {
            log.error("Operation failed with fault tolerance: {}",
                    operationName, e);
            throw e;
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    /**
     * 构建容错链
     */
    private <T> FaultToleranceChain<T> buildFaultToleranceChain(
            String operationName,
            Supplier<T> operation,
            FaultToleranceConfig config) {

        FaultToleranceChain<T> chain = new FaultToleranceChain<>();

        // 1. 添加熔断器
        if (config.isCircuitBreakerEnabled()) {
            CircuitBreaker breaker = getOrCreateCircuitBreaker(
                    operationName,
                    config.getCircuitBreakerConfig()
            );
            chain.addCircuitBreaker(breaker);
        }

        // 2. 添加重试策略
        if (config.isRetryEnabled()) {
            Retry retry = getOrCreateRetry(
                    operationName,
                    config.getRetryConfig()
            );
            chain.addRetry(retry);
        }

        // 3. 添加隔板模式
        if (config.isBulkheadEnabled()) {
            Bulkhead bulkhead = getOrCreateBulkhead(
                    operationName,
                    config.getBulkheadConfig()
            );
            chain.addBulkhead(bulkhead);
        }

        // 4. 添加降级策略
        if (config.isFallbackEnabled()) {
            Fallback<T> fallback = getOrCreateFallback(
                    operationName,
                    config.getFallbackConfig()
            );
            chain.addFallback(fallback);
        }

        // 5. 设置操作
        chain.setOperation(operation);

        return chain;
    }

    /**
     * 获取或创建熔断器
     */
    private CircuitBreaker getOrCreateCircuitBreaker(
            String name,
            CircuitBreakerConfig config) {
        return circuitBreakerRegistry.circuitBreaker(name, config);
    }

    /**
     * 获取或创建重试策略
     */
    private Retry getOrCreateRetry(
            String name,
            RetryConfig config) {
        return retryRegistry.retry(name, config);
    }

    /**
     * 获取或创建隔板
     */
    private Bulkhead getOrCreateBulkhead(
            String name,
            BulkheadConfig config) {
        return bulkheadRegistry.bulkhead(name, config);
    }

    /**
     * 获取或创建降级策略
     */
    private <T> Fallback<T> getOrCreateFallback(
            String name,
            FallbackConfig config) {
        return fallbackRegistry.fallback(name, config);
    }
}