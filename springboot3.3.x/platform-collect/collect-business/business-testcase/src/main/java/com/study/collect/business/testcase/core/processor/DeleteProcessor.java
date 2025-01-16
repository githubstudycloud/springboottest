package com.study.collect.business.testcase.core.processor;

import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.utils.ListCompareUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * URI删除处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteProcessor implements DataProcessor<DeleteParam, Long> {

    private final UriRepository repository;
    private final Map<String, ProcessorStatus> taskStatusMap = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Long> process(DeleteParam param) {
        // 初始化处理状态
        ProcessorStatus status = new ProcessorStatus();
        taskStatusMap.put(param.getTaskId(), status);

        CompletableFuture<Long> future = new CompletableFuture<>();
        try {
            // 分批处理
            List<List<String>> batches = ListCompareUtil.partition(param.getUris(), param.getBatchSize());
            long totalDeleted = 0;
            long totalBatches = batches.size();

            for (int i = 0; i < batches.size() && !status.cancelled; i++) {
                List<String> batch = batches.get(i);

                // 检查是否暂停
                while (status.paused && !status.cancelled) {
                    Thread.sleep(100);
                }

                if (status.cancelled) {
                    break;
                }

                // 执行删除
                long batchCount = param.getHardDelete() ?
                        repository.batchHardDelete(param.getRootNode(), batch) :
                        repository.batchSoftDelete(param.getRootNode(), batch);

                totalDeleted += batchCount;

                // 更新进度
                double progress = (i + 1.0) / totalBatches;
                status.update(
                        String.format("Processed %d/%d batches", i + 1, totalBatches),
                        progress
                );
            }

            if (status.cancelled) {
                future.complete(totalDeleted);
            } else {
                status.update("Completed", 1.0);
                future.complete(totalDeleted);
            }

        } catch (Exception e) {
            status.error(e.getMessage());
            future.completeExceptionally(e);
        }

        return future;
    }

    @Override
    public boolean cancel(String taskId) {
        ProcessorStatus status = taskStatusMap.get(taskId);
        if (status != null && !status.isCompleted()) {
            status.cancel();
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePriority(String taskId, int priority) {
        // 删除处理器不支持优先级调整
        return false;
    }

    @Override
    public StreamProcessor.ProcessMetrics getProgress(String taskId) {
        ProcessorStatus status = taskStatusMap.get(taskId);
        if (status != null) {
            return status.toMetrics();
        }
        return null;
    }

    @Override
    public void pause(String taskId) {
        ProcessorStatus status = taskStatusMap.get(taskId);
        if (status != null) {
            status.pause();
        }
    }

    @Override
    public void resume(String taskId) {
        ProcessorStatus status = taskStatusMap.get(taskId);
        if (status != null) {
            status.resume();
        }
    }

    /**
     * 处理器状态类
     */
    private static class ProcessorStatus {
        private String stage;
        private double progress;
        private String error;
        private boolean completed;
        private boolean cancelled;
        private boolean paused;
        private final long startTime;
        private Long endTime;

        ProcessorStatus() {
            this.startTime = System.currentTimeMillis();
            this.progress = 0;
            this.stage = "Initializing";
        }

        void update(String stage, double progress) {
            this.stage = stage;
            this.progress = progress;
            if (progress >= 1.0) {
                this.completed = true;
                this.endTime = System.currentTimeMillis();
            }
        }

        void error(String message) {
            this.error = message;
            this.completed = true;
            this.endTime = System.currentTimeMillis();
        }

        void cancel() {
            this.cancelled = true;
            this.completed = true;
            this.endTime = System.currentTimeMillis();
        }

        void pause() {
            this.paused = true;
        }

        void resume() {
            this.paused = false;
        }

        boolean isCompleted() {
            return completed;
        }

        StreamProcessor.ProcessMetrics toMetrics() {
            return StreamProcessor.ProcessMetrics.builder()
                    .processorName("URI-Delete")
                    .startTime(startTime)
                    .endTime(endTime)
                    .progressPercentage(progress * 100)
                    .customMetrics(Map.of(
                            "stage", stage,
                            "error", error,
                            "cancelled", cancelled,
                            "paused", paused
                    ))
                    .build();
        }
    }
}