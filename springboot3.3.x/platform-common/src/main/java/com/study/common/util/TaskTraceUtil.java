package com.study.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * 链路追踪工具类
 */
public class TaskTraceUtil {
    private static final Logger logger = LoggerFactory.getLogger(TaskTraceUtil.class);
    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";
    private static final String PARENT_SPAN_ID = "parentSpanId";

    /**
     * 生成追踪ID
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成Span ID
     */
    public static String generateSpanId() {
        return Long.toHexString(UUID.randomUUID().getMostSignificantBits());
    }

    /**
     * 开始追踪
     */
    public static void startTrace() {
        startTrace(generateTraceId());
    }

    /**
     * 使用指定的追踪ID开始追踪
     */
    public static void startTrace(String traceId) {
        MDC.put(TRACE_ID, traceId);
        MDC.put(SPAN_ID, generateSpanId());
    }

    /**
     * 开始新的Span
     */
    public static String startSpan() {
        String parentSpanId = MDC.get(SPAN_ID);
        String newSpanId = generateSpanId();
        MDC.put(PARENT_SPAN_ID, parentSpanId);
        MDC.put(SPAN_ID, newSpanId);
        return newSpanId;
    }

    /**
     * 结束当前Span
     */
    public static void endSpan() {
        String parentSpanId = MDC.get(PARENT_SPAN_ID);
        if (parentSpanId != null) {
            MDC.put(SPAN_ID, parentSpanId);
        }
        MDC.remove(PARENT_SPAN_ID);
    }

    /**
     * 清理追踪上下文
     */
    public static void clearTrace() {
        MDC.clear();
    }

    /**
     * 在追踪上下文中执行
     */
    public static <T> T executeWithTrace(Supplier<T> supplier) {
        boolean isNewTrace = MDC.get(TRACE_ID) == null;
        if (isNewTrace) {
            startTrace();
        }

        try {
            return supplier.get();
        } finally {
            if (isNewTrace) {
                clearTrace();
            }
        }
    }

    /**
     * 获取当前追踪ID
     */
    public static String currentTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 获取当前Span ID
     */
    public static String currentSpanId() {
        return MDC.get(SPAN_ID);
    }
}