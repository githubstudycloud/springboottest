package com.study.collect.business.testcase.core.processor;

import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.http.UriHttpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * URI采集处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CollectProcessor implements DataProcessor<CollectParam, Long> {

    private final UriHttpService httpService;
    private final UriRepository repository;

    private final Map<String, ProcessorStatus> taskStatusMap = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Long> process(CollectParam param) {
        // 初始化处理状态
        ProcessorStatus status = new ProcessorStatus();
        taskStatusMap.put(param.getTaskId(), status);

        CompletableFuture<Long> future = new CompletableFuture<>();
        try {
            // 获取版本列表
            httpService.getAllVersions(param)
                    .thenCompose(versions -> {
                        // 更新进度
                        status.update("Getting URIs for versions", 0.2);

                        // 获取每个版本的URI列表
                        List<CompletableFuture<List<String>>> uriFutures = versions.stream()
                                .map(version -> httpService.getAllUrisForVersion(param, version))
                                .collect(Collectors.toList());

                        return CompletableFuture.allOf(uriFutures.toArray(new CompletableFuture[0]))
                                .thenApply(v -> uriFutures.stream()
                                        .map(CompletableFuture::join)
                                        .flatMap(List::stream)
                                        .collect(Collectors.toList()));
                    })
                    .thenCompose(uris -> {
                        // 更新进度
                        status.update("Getting URI details", 0.4);

                        // 获取URI详情
                        return httpService.batchGetUriDetails(param, uris, param.getBatchSize());
                    })
                    .thenAccept(details -> {
                        // 更新进度
                        status.update("Saving to database", 0.8);

                        // 保存到数据库
                        long savedCount = saveToDatabase(param.getRootNode(), details);
                        status.update("Completed", 1.0);

                        future.complete(savedCount);
                    })
                    .exceptionally(throwable -> {
                        status.error(throwable.getMessage());
                        future.completeExceptionally(throwable);
                        return null;
                    });

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
        // 采集处理器不支持优先级调整
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
                    .processorName("URI-Collect")
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