package com.study.collect.core.processor.chain;

import com.study.collect.core.processor.ProcessContext;
import com.study.collect.core.processor.chain.ProcessorChain;
import com.study.collect.core.processor.chain.ProcessorChainBuilder;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 数据处理流程管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataProcessManager {

    private final ProcessorChainBuilder chainBuilder;
    private final ProcessorRegistry processorRegistry;
    private final MetricsCollector metricsCollector;
    private final CacheManager cacheManager;

    /**
     * 执行处理流程
     */
    public ProcessResult process(ProcessRequest request) {
        String flowId = UUID.randomUUID().toString();
        Timer.Sample timer = metricsCollector.startTimer("process_flow");

        try {
            // 1. 构建处理上下文
            ProcessContext context = buildContext(request, flowId);

            // 2. 获取处理器链
            ProcessorChain chain = getProcessorChain(request);

            // 3. 执行处理流程
            return executeFlow(chain, context);

        } catch (Exception e) {
            log.error("Process flow failed: {}", flowId, e);
            return ProcessResult.failed(e.getMessage());
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    private ProcessContext buildContext(ProcessRequest request, String flowId) {
        return ProcessContext.builder()
                .flowId(flowId)
                .data(request.getData())
                .type(request.getType())
                .params(request.getParams())
                .startTime(LocalDateTime.now())
                .build();
    }

    private ProcessorChain getProcessorChain(ProcessRequest request) {
        // 1. 尝试从缓存获取
        String cacheKey = "processor_chain:" + request.getType();
        ProcessorChain chain = cacheManager.get(cacheKey, ProcessorChain.class);
        if (chain != null) {
            return chain;
        }

        // 2. 构建处理器链
        List<Processor> processors = processorRegistry.getProcessors(request.getType());
        chain = chainBuilder.build(processors);

        // 3. 缓存处理器链
        cacheManager.set(cacheKey, chain, 3600); // 1小时缓存

        return chain;
    }

    private ProcessResult executeFlow(ProcessorChain chain, ProcessContext context) {
        try {
            // 1. 前置处理
            preProcess(context);

            // 2. 执行处理链
            chain.process(context);

            // 3. 后置处理
            postProcess(context);

            // 4. 构建结果
            return buildResult(context);

        } catch (Exception e) {
            log.error("Execute flow failed: {}", context.getFlowId(), e);
            handleProcessError(context, e);
            throw e;
        } finally {
            // 记录处理时间
            long duration = Duration.between(
                    context.getStartTime(),
                    LocalDateTime.now()
            ).toMillis();
            metricsCollector.recordProcessDuration(duration);
        }
    }

    private void preProcess(ProcessContext context) {
        // 1. 记录开始时间
        context.setStartTime(LocalDateTime.now());

        // 2. 初始化处理状态
        context.setStatus(ProcessStatus.PROCESSING);

        // 3. 记录处理开始
        saveProcessLog(context, "开始处理数据");
    }

    private void postProcess(ProcessContext context) {
        // 1. 更新处理状态
        context.setStatus(ProcessStatus.COMPLETED);
        context.setEndTime(LocalDateTime.now());

        // 2. 记录处理完成
        saveProcessLog(context, "数据处理完成");

        // 3. 清理上下文
        cleanupContext(context);
    }

    private void handleProcessError(ProcessContext context, Exception e) {
        // 1. 更新处理状态
        context.setStatus(ProcessStatus.FAILED);
        context.setEndTime(LocalDateTime.now());
        context.setError(e.getMessage());

        // 2. 记录错误日志
        saveProcessLog(context, "处理失败: " + e.getMessage());

        // 3. 增加错误计数
        metricsCollector.incrementProcessError(context.getType());
    }

    private void saveProcessLog(ProcessContext context, String message) {
        ProcessLog log = ProcessLog.builder()
                .flowId(context.getFlowId())
                .type(context.getType())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        // 异步保存日志
        CompletableFuture.runAsync(() -> {
            try {
                processLogRepository.save(log);
            } catch (Exception e) {
                log.error("Save process log failed", e);
            }
        });
    }
}

