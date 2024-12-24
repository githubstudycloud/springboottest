package com.study.collect.core.task.monitor;

import com.study.collect.core.task.model.TaskStatus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Slf4j
@Component
public class TaskMonitor {

    private final Map<TaskStatus, Counter> statusCounters = new EnumMap<>(TaskStatus.class);
    private final Counter totalTaskCounter;
    private final Counter failedTaskCounter;
    private final Counter timeoutTaskCounter;

    public TaskMonitor(MeterRegistry registry) {
        // 初始化计数器
        totalTaskCounter = Counter.builder("task.total")
                .description("总任务数")
                .register(registry);

        failedTaskCounter = Counter.builder("task.failed")
                .description("失败任务数")
                .register(registry);

        timeoutTaskCounter = Counter.builder("task.timeout")
                .description("超时任务数")
                .register(registry);

        // 初始化状态计数器
        for (TaskStatus status : TaskStatus.values()) {
            statusCounters.put(status, Counter.builder("task.status")
                    .tag("status", status.name())
                    .description("任务状态统计")
                    .register(registry));
        }
    }

    public void recordTaskSubmit() {
        totalTaskCounter.increment();
    }

    public void recordTaskStatus(TaskStatus status) {
        statusCounters.get(status).increment();
    }

    public void recordTaskFailed() {
        failedTaskCounter.increment();
    }

    public void recordTaskTimeout() {
        timeoutTaskCounter.increment();
    }
}