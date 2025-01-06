package com.study.collect.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class BatchProcessUtil {
    private static final int DEFAULT_BATCH_SIZE = 1000;
    private static final int DEFAULT_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2;
    private static final long DEFAULT_TIMEOUT = 1L;
    private static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.HOURS;

    /**
     * 处理接口，用于批量处理数据
     */
    @FunctionalInterface
    public interface BatchProcessor<T> {
        void process(List<T> batch) throws Exception;
    }

    /**
     * 异步分批处理列表数据
     *
     * @param list      待处理的列表
     * @param processor 处理器
     * @param <T>       数据类型
     */
    public static <T> void processBatch(List<T> list, BatchProcessor<T> processor) {
        processBatch(list, DEFAULT_BATCH_SIZE, processor);
    }

    /**
     * 异步分批处理列表数据
     *
     * @param list       待处理的列表
     * @param batchSize  批次大小
     * @param processor  处理器
     * @param <T>       数据类型
     */
    public static <T> void processBatch(List<T> list, int batchSize, BatchProcessor<T> processor) {
        processBatch(list, batchSize, DEFAULT_THREAD_COUNT, processor);
    }

    /**
     * 异步分批处理列表数据
     *
     * @param list          待处理的列表
     * @param batchSize     批次大小
     * @param threadCount   线程数
     * @param processor     处理器
     * @param <T>          数据类型
     */
    public static <T> void processBatch(List<T> list, int batchSize, int threadCount, BatchProcessor<T> processor) {
        if (list == null || list.isEmpty()) {
            return;
        }

        // 创建线程池
        ExecutorService executorService = new ThreadPoolExecutor(
            threadCount, threadCount,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(5000),
            new ThreadFactoryBuilder().setNameFormat("batch-process-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );

        try {
            // 计算总批次数
            int totalBatches = (list.size() + batchSize - 1) / batchSize;
            CountDownLatch latch = new CountDownLatch(totalBatches);
            List<Future<?>> futures = new ArrayList<>(totalBatches);

            // 分批提交任务
            for (int i = 0; i < list.size(); i += batchSize) {
                final int start = i;
                final int end = Math.min(start + batchSize, list.size());
                List<T> batch = list.subList(start, end);

                Future<?> future = executorService.submit(() -> {
                    try {
                        processor.process(batch);
                    } catch (Exception e) {
                        log.error("Error processing batch [{}, {}]", start, end, e);
                        throw new RuntimeException(e);
                    } finally {
                        latch.countDown();
                    }
                });
                futures.add(future);
            }

            // 等待所有任务完成
            if (!latch.await(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)) {
                log.warn("Batch processing timeout after {} {}", DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT);
            }

            // 检查是否有任务异常
            for (Future<?> future : futures) {
                try {
                    future.get(0, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    log.error("Task execution failed", e);
                }
            }

        } catch (Exception e) {
            log.error("Error in batch processing", e);
            throw new RuntimeException("Batch processing failed", e);
        } finally {
            executorService.shutdownNow();
        }
    }

    /**
     * MongoDB批量保存工具方法
     * @param list 数据列表
     * @param repository MongoDB仓库
     * @param <T> 实体类型
     */
    public static <T> void saveToMongo(List<T> list, MongoRepository<T, String> repository) {
        processBatch(list, DEFAULT_BATCH_SIZE, batch -> repository.saveAll(batch));
    }

    /**
     * MongoDB批量保存工具方法（支持转换）
     * @param list 数据列表
     * @param converter 转换函数
     * @param repository MongoDB仓库
     * @param <S> 源数据类型
     * @param <T> 目标实体类型
     */
    public static <S, T> void saveToMongo(List<S> list,
                                        Function<S, T> converter,
                                        MongoRepository<T, String> repository) {
        processBatch(list, DEFAULT_BATCH_SIZE, batch -> {
            List<T> entities = new ArrayList<>(batch.size());
            for (S source : batch) {
                entities.add(converter.apply(source));
            }
            repository.saveAll(entities);
        });
    }
}

// 使用示例：
@Service
public class UriCollectServiceImpl {
    
    public void saveUriDetails(List<Map<String, Object>> details) {
        // 方式1：直接使用处理器
        BatchProcessUtil.processBatch(details, 1000, batch -> {
            List<UriEntity> entities = new ArrayList<>(batch.size());
            for (Map<String, Object> detail : batch) {
                UriEntity entity = convertToEntity(detail);
                entities.add(entity);
            }
            repository.saveAll(entities);
        });

        // 方式2：使用转换函数
        BatchProcessUtil.saveToMongo(
            details,
            this::convertToEntity,  // 转换函数
            repository             // MongoDB仓库
        );
    }

    private UriEntity convertToEntity(Map<String, Object> detail) {
        UriEntity entity = new UriEntity();
        entity.setUri((String) detail.get("uri"));
        // ... 设置其他字段
        return entity;
    }
}
