package com.study.collect.core.engine;

import com.study.collect.common.enums.collect.TaskStatus;
import com.study.collect.core.processor.impl.storage.MongoProcessor;

import java.util.Arrays;

/**
 * 标准采集引擎实现
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StandardCollectEngine extends AbstractCollectEngine {

    private final CollectorFactory collectorFactory;
    private final ProcessorChainBuilder chainBuilder;
    private final TaskRepository taskRepository;
    private final MetricsCollector metricsCollector;

    @Override
    protected CollectResult doCollect(CollectContext context) {
        Timer.Sample sample = metricsCollector.startTimer();
        try {
            // 获取采集器
            Collector collector = collectorFactory.getCollector(context.getCollectType());

            // 构建处理器链
            ProcessorChain chain = chainBuilder.build(context.getProcessors());

            // 执行采集
            Object data = collector.collect(context);

            // 执行处理链
            ProcessContext processContext = new ProcessContext(data, context.getParams());
            chain.process(processContext);

            // 返回结果
            return CollectResult.success(processContext.getResult());
        } finally {
            metricsCollector.stopTimer(sample);
        }
    }

    @Override
    protected List<Processor> getDefaultProcessors(CollectType type) {
        switch (type) {
            case TREE:
                return Arrays.asList(
                        new TreeTransformer(),
                        new TreeFilter(),
                        new TreeValidator(),
                        new CacheProcessor(),
                        new MongoProcessor()
                );
            case LIST:
                return Arrays.asList(
                        new ListTransformer(),
                        new ListFilter(),
                        new ListValidator(),
                        new CacheProcessor(),
                        new MongoProcessor()
                );
            default:
                return Arrays.asList(
                        new DataTransformer(),
                        new DataValidator(),
                        new CacheProcessor(),
                        new MongoProcessor()
                );
        }
    }

    @Override
    public void stop(String taskId) {
        try {
            taskRepository.updateStatus(taskId, TaskStatus.CANCELED);
            log.info("Task stopped: {}", taskId);
        } catch (Exception e) {
            log.error("Failed to stop task", e);
            throw new TaskException("Failed to stop task: " + taskId, e);
        }
    }

    @Override
    public TaskStatus getStatus(String taskId) {
        try {
            Optional<CollectTask> task = taskRepository.findById(taskId);
            return task.map(t -> TaskStatus.valueOf(t.getStatus()))
                    .orElseThrow(() -> new TaskException("Task not found: " + taskId));
        } catch (Exception e) {
            log.error("Failed to get task status", e);
            throw new TaskException("Failed to get task status: " + taskId, e);
        }
    }
}