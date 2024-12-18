package com.study.collect.core.strategy;

import com.study.collect.common.annotation.lock.DistributedLock;
import com.study.collect.common.exception.collect.ProcessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 分布式数据处理协调器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedProcessCoordinator {

    private final NodeManager nodeManager;
    private final ProcessManager processManager;
    private final LoadBalancer loadBalancer;
    private final DistributedLock distributedLock;

    /**
     * 执行分布式处理
     */
    public ProcessResult processDistributed(ProcessRequest request) {
        String processId = UUID.randomUUID().toString();
        String lockKey = "process:lock:" + processId;

        try {
            // 1. 获取分布式锁
            if (!distributedLock.lock(lockKey, 30000)) {
                throw new ProcessException("Failed to acquire process lock");
            }

            // 2. 创建处理计划
            ProcessPlan plan = createProcessPlan(request);

            // 3. 分发处理任务
            List<Future<ProcessResult>> futures = distributeProcessTasks(plan);

            // 4. 收集处理结果
            return aggregateResults(futures);

        } finally {
            distributedLock.unlock(lockKey);
        }
    }

    private ProcessPlan createProcessPlan(ProcessRequest request) {
        // 1. 获取可用节点
        List<NodeInfo> nodes = nodeManager.getActiveNodes();
        if (nodes.isEmpty()) {
            throw new ProcessException("No available nodes for processing");
        }

        // 2. 划分数据分片
        List<DataShard> shards = splitDataShards(request.getData(), nodes.size());

        // 3. 创建处理计划
        return ProcessPlan.builder()
                .processId(UUID.randomUUID().toString())
                .shards(shards)
                .nodes(nodes)
                .startTime(LocalDateTime.now())
                .timeout(request.getTimeout())
                .build();
    }

    private List<Future<ProcessResult>> distributeProcessTasks(ProcessPlan plan) {
        List<Future<ProcessResult>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(plan.getShards().size());

        try {
            // 为每个分片创建处理任务
            for (int i = 0; i < plan.getShards().size(); i++) {
                DataShard shard = plan.getShards().get(i);
                NodeInfo node = selectProcessNode(plan.getNodes(), shard);

                Future<ProcessResult> future = executor.submit(() ->
                        processDataShard(shard, node));
                futures.add(future);
            }

            return futures;
        } finally {
            executor.shutdown();
        }
    }

    private NodeInfo selectProcessNode(List<NodeInfo> nodes, DataShard shard) {
        // 1. 计算负载权重
        Map<NodeInfo, Double> weights = nodes.stream()
                .collect(Collectors.toMap(
                        node -> node,
                        this::calculateNodeWeight
                ));

        // 2. 选择最适合的节点
        return weights.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new ProcessException("No suitable node found"));
    }

    private double calculateNodeWeight(NodeInfo node) {
        // 1. 基础权重
        double weight = 1.0;

        // 2. 考虑CPU使用率
        weight *= (1 - node.getCpuUsage() / 100.0);

        // 3. 考虑内存使用率
        weight *= (1 - node.getMemoryUsage() / 100.0);

        // 4. 考虑当前处理任务数
        weight *= (1 - node.getTaskCount() / 100.0);

        return weight;
    }

    private ProcessResult processDataShard(DataShard shard, NodeInfo node) {
        try {
            // 1. 构建处理请求
            ProcessRequest request = ProcessRequest.builder()
                    .shardId(shard.getId())
                    .data(shard.getData())
                    .params(shard.getParams())
                    .timeout(shard.getTimeout())
                    .build();

            // 2. 发送处理请求
            return processManager.process(request);

        } catch (Exception e) {
            log.error("Process shard failed: {}", shard.getId(), e);
            return ProcessResult.failed(e.getMessage());
        }
    }

    private ProcessResult aggregateResults(List<Future<ProcessResult>> futures)
            throws ProcessException {

        List<ProcessResult> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // 1. 收集所有分片结果
        for (Future<ProcessResult> future : futures) {
            try {
                ProcessResult result = future.get(30, TimeUnit.SECONDS);
                if (result.isSuccess()) {
                    results.add(result);
                } else {
                    errors.add(result.getError());
                }
            } catch (Exception e) {
                errors.add(e.getMessage());
            }
        }

        // 2. 检查处理结果
        if (!errors.isEmpty()) {
            throw new ProcessException("Some shards failed: " + String.join(", ", errors));
        }

        // 3. 合并处理结果
        return mergeResults(results);
    }

    private ProcessResult mergeResults(List<ProcessResult> results) {
        try {
            // 1. 准备合并
            Object mergedData = null;
            Map<String, Object> mergedStats = new HashMap<>();

            // 2. 合并数据
            for (ProcessResult result : results) {
                if (mergedData == null) {
                    mergedData = result.getData();
                } else {
                    mergedData = mergeData(mergedData, result.getData());
                }

                // 合并统计信息
                mergeStats(mergedStats, result.getStats());
            }

            // 3. 构建最终结果
            return ProcessResult.success(mergedData)
                    .withStats(mergedStats);

        } catch (Exception e) {
            log.error("Merge results failed", e);
            throw new ProcessException("Merge results failed: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Object mergeData(Object data1, Object data2) {
        // 根据数据类型选择合并策略
        if (data1 instanceof List) {
            List<Object> merged = new ArrayList<>((List<Object>) data1);
            merged.addAll((List<Object>) data2);
            return merged;
        } else if (data1 instanceof Map) {
            Map<String, Object> merged = new HashMap<>((Map<String, Object>) data1);
            merged.putAll((Map<String, Object>) data2);
            return merged;
        } else {
            throw new ProcessException("Unsupported data type for merge");
        }
    }

    private void mergeStats(Map<String, Object> stats1, Map<String, Object> stats2) {
        stats2.forEach((key, value) -> {
            if (value instanceof Number) {
                // 数值类型统计信息直接相加
                double val1 = stats1.containsKey(key) ?
                        ((Number) stats1.get(key)).doubleValue() : 0;
                double val2 = ((Number) value).doubleValue();
                stats1.put(key, val1 + val2);
            } else {
                // 其他类型保留最新值
                stats1.put(key, value);
            }
        });
    }
}