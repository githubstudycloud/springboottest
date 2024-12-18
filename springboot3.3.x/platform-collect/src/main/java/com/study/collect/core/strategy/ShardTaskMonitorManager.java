package com.study.collect.core.strategy;

import com.study.collect.common.enums.collect.TaskStatus;
import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.domain.repository.task.TaskRepository;
import com.study.collect.infrastructure.monitor.alert.AlertManager;
import com.study.collect.infrastructure.monitor.metrics.collector.SystemMetrics;
import com.study.collect.infrastructure.monitor.metrics.collector.TaskMetrics;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 分片任务监控管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShardTaskMonitorManager {

    private final MetricsCollector metricsCollector;
    private final AlertManager alertManager;
    private final HealthChecker healthChecker;
    private final TaskRepository taskRepository;

    // 监控线程池
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(3);

    @PostConstruct
    public void init() {
        // 启动监控任务
        startMonitoring();
    }

    /**
     * 启动监控
     */
    private void startMonitoring() {
        // 1. 任务执行监控
        scheduler.scheduleAtFixedRate(
                this::monitorTaskExecution,
                0, 30, TimeUnit.SECONDS
        );

        // 2. 系统资源监控
        scheduler.scheduleAtFixedRate(
                this::monitorSystemResources,
                0, 60, TimeUnit.SECONDS
        );

        // 3. 异常监控
        scheduler.scheduleAtFixedRate(
                this::monitorAnomalies,
                0, 120, TimeUnit.SECONDS
        );
    }

    /**
     * 监控任务执行
     */
    private void monitorTaskExecution() {
        try {
            // 1. 收集任务执行指标
            TaskMetrics metrics = collectTaskMetrics();

            // 2. 检查执行异常
            checkTaskExecution(metrics);

            // 3. 更新监控指标
            updateTaskMetrics(metrics);

            // 4. 处理告警
            handleTaskAlerts(metrics);

        } catch (Exception e) {
            log.error("Monitor task execution failed", e);
        }
    }

    /**
     * 收集任务执行指标
     */
    private TaskMetrics collectTaskMetrics() {
        // 1. 获取最近任务
        List<CollectTask> recentTasks =
                taskRepository.findRecentTasks(Duration.ofMinutes(5));

        // 2. 计算执行指标
        return TaskMetrics.builder()
                .totalTasks(recentTasks.size())
                .successCount(countTasksByStatus(recentTasks, TaskStatus.SUCCESS))
                .failureCount(countTasksByStatus(recentTasks, TaskStatus.FAILED))
                .averageExecutionTime(calculateAverageExecutionTime(recentTasks))
                .maxExecutionTime(calculateMaxExecutionTime(recentTasks))
                .taskQueueSize(taskRepository.getQueueSize())
                .activeShards(countActiveShards(recentTasks))
                .build();
    }

    /**
     * 检查任务执行异常
     */
    private void checkTaskExecution(TaskMetrics metrics) {
        // 1. 检查失败率
        double failureRate = calculateFailureRate(
                metrics.getSuccessCount(),
                metrics.getFailureCount()
        );

        if (failureRate > 0.2) { // 失败率超过20%
            alertManager.sendAlert(Alert.builder()
                    .level(AlertLevel.WARNING)
                    .type("HIGH_FAILURE_RATE")
                    .message("Task failure rate too high: " +
                            String.format("%.2f%%", failureRate * 100))
                    .metrics(metrics)
                    .build()
            );
        }

        // 2. 检查执行时间
        if (metrics.getMaxExecutionTime() > 300000) { // 超过5分钟
            alertManager.sendAlert(Alert.builder()
                    .level(AlertLevel.WARNING)
                    .type("LONG_EXECUTION_TIME")
                    .message("Task execution time too long: " +
                            metrics.getMaxExecutionTime() + "ms")
                    .metrics(metrics)
                    .build()
            );
        }

        // 3. 检查队列积压
        if (metrics.getTaskQueueSize() > 1000) {
            alertManager.sendAlert(Alert.builder()
                    .level(AlertLevel.CRITICAL)
                    .type("QUEUE_OVERFLOW")
                    .message("Task queue size too large: " +
                            metrics.getTaskQueueSize())
                    .metrics(metrics)
                    .build()
            );
        }
    }

    /**
     * 监控系统资源
     */
    private void monitorSystemResources() {
        try {
            // 1. 收集系统指标
            SystemMetrics metrics = healthChecker.checkSystem();

            // 2. 更新监控指标
            updateSystemMetrics(metrics);

            // 3. 检查资源告警
            checkResourceAlerts(metrics);

        } catch (Exception e) {
            log.error("Monitor system resources failed", e);
        }
    }

    /**
     * 检查资源告警
     */
    private void checkResourceAlerts(SystemMetrics metrics) {
        // 1. 检查CPU使用率
        if (metrics.getCpuUsage() > 80) {
            alertManager.sendAlert(Alert.builder()
                    .level(AlertLevel.WARNING)
                    .type("HIGH_CPU_USAGE")
                    .message("CPU usage too high: " +
                            metrics.getCpuUsage() + "%")
                    .metrics(metrics)
                    .build()
            );
        }

        // 2. 检查内存使用率
        if (metrics.getMemoryUsage() > 85) {
            alertManager.sendAlert(Alert.builder()
                    .level(AlertLevel.WARNING)
                    .type("HIGH_MEMORY_USAGE")
                    .message("Memory usage too high: " +
                            metrics.getMemoryUsage() + "%")
                    .metrics(metrics)
                    .build()
            );
        }

        // 3. 检查磁盘使用率
        if (metrics.getDiskUsage() > 90) {
            alertManager.sendAlert(Alert.builder()
                    .level(AlertLevel.CRITICAL)
                    .type("HIGH_DISK_USAGE")
                    .message("Disk usage too high: " +
                            metrics.getDiskUsage() + "%")
                    .metrics(metrics)
                    .build()
            );
        }
    }

    /**
     * 更新监控指标
     */
    private void updateTaskMetrics(TaskMetrics metrics) {
        // 更新Prometheus指标
        metricsCollector.gauge("task_total", metrics.getTotalTasks());
        metricsCollector.gauge("task_success", metrics.getSuccessCount());
        metricsCollector.gauge("task_failure", metrics.getFailureCount());
        metricsCollector.gauge("task_avg_time",
                metrics.getAverageExecutionTime());
        metricsCollector.gauge("task_max_time",
                metrics.getMaxExecutionTime());
        metricsCollector.gauge("task_queue_size",
                metrics.getTaskQueueSize());
        metricsCollector.gauge("task_active_shards",
                metrics.getActiveShards());
    }

    /**
     * 更新系统指标
     */
    private void updateSystemMetrics(SystemMetrics metrics) {
        metricsCollector.gauge("system_cpu_usage", metrics.getCpuUsage());
        metricsCollector.gauge("system_memory_usage",
                metrics.getMemoryUsage());
        metricsCollector.gauge("system_disk_usage", metrics.getDiskUsage());
        metricsCollector.gauge("system_network_io",
                metrics.getNetworkIO());
        metricsCollector.gauge("system_load_average",
                metrics.getLoadAverage());
    }
}

