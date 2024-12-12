package com.study.common.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

class TaskTraceUtilTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void startTrace_ShouldSetTraceIdAndSpanId() {
        TaskTraceUtil.startTrace();

        assertNotNull(MDC.get("traceId"));
        assertNotNull(MDC.get("spanId"));
    }

    @Test
    void startSpan_ShouldCreateNewSpanAndPreserveParent() {
        TaskTraceUtil.startTrace();
        String originalSpanId = MDC.get("spanId");

        String newSpanId = TaskTraceUtil.startSpan();

        assertNotNull(newSpanId);
        assertNotEquals(originalSpanId, newSpanId);
        assertEquals(originalSpanId, MDC.get("parentSpanId"));
    }

    @Test
    void executeWithTrace_ShouldMaintainTraceContext() {
        String result = TaskTraceUtil.executeWithTrace(() -> {
            assertNotNull(MDC.get("traceId"));
            assertNotNull(MDC.get("spanId"));
            return "success";
        });

        assertEquals("success", result);
        assertNull(MDC.get("traceId")); // Context should be cleared
    }

    @Test
    void executeWithTrace_ShouldHandleException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                TaskTraceUtil.executeWithTrace(() -> {
                    throw new RuntimeException("test");
                })
        );

        assertEquals("test", exception.getMessage());
        assertNull(MDC.get("traceId")); // Context should be cleared even on exception
    }
}