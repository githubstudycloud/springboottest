package com.study.collect.infrastructure.monitor.metrics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/**
 * 监控切面
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MonitoringAspect {

    private final MetricsCollector metricsCollector;

    @Around("@annotation(monitoring)")
    public Object around(ProceedingJoinPoint point, Monitoring monitoring) throws Throwable {
        Timer.Sample sample = metricsCollector.startTimer();
        try {
            Object result = point.proceed();
            metricsCollector.incrementSuccessCount();
            return result;
        } catch (Exception e) {
            metricsCollector.incrementFailureCount();
            throw e;
        } finally {
            metricsCollector.stopTimer(sample);
        }
    }
}
