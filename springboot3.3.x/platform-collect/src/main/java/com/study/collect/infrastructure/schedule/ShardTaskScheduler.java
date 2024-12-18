package com.study.collect.infrastructure.schedule;

import com.study.collect.common.annotation.lock.DistributedLock;
import com.study.collect.core.executor.ShardTaskExecutor;
import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.domain.repository.task.TaskRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 分片任务调度器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShardTaskScheduler {

    private final NodeManager nodeManager;
    private final TaskRepository taskRepository;
    private final ShardTaskExecutor shardExecutor;
    private final LoadBalancer loadBalancer;
    private final DistributedLock distributedLock;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(2);

    @PostConstruct
    public void init() {
        // 启动调度任务
        startScheduling();
    }

    /**
     * 启动调度
     */
    private void startScheduling() {
        // 1. 任务分发调度
        scheduler.scheduleWithFixedDelay(
                this::scheduleShardTasks,
                0, 100, TimeUnit.MILLISECONDS
        );

        // 2. 节点健康检查
        scheduler.scheduleWithFixedDelay(
                this::checkNodeHealth,
                0, 30, TimeUnit.SECONDS
        );
    }

    /**
     * 调度分片任务
     */
    private void scheduleShardTasks() {
        String lockKey = "shard_task_schedule_lock";
        try {
            // 1. 获取调度锁
            if (!distributedLock.tryLock(lockKey, 5000)) {
                return;
            }

            try {
                // 2. 获取待处理任务
                List<CollectTask> pendingTasks =
                        taskRepository.findPendingTasks(100);

                // 3. 获取可用节点
                List<NodeInfo> availableNodes =
                        nodeManager.getActiveNodes();
                if (availableNodes.isEmpty()) {
                    log.warn("No available nodes for task scheduling");
                    return;
                }

                // 4. 分发任务
                distributeTasks(pendingTasks, availableNodes);

            } finally {
                distributedLock.unlock(lockKey);
            }
        } catch (Exception e) {
            log.error("Schedule shard tasks failed", e);
        }
    }

    /**
     * 分发任务
     */
    private void distributeTasks(List<CollectTask> tasks,
                                 List<NodeInfo> nodes) {
        // 1. 计算节点负载情况
        Map<NodeInfo, Integer> nodeTasks = calculateNodeTasks(nodes);

        // 2. 按优先级排序任务
        List<CollectTask> sortedTasks = sortTasksByPriority(tasks);

        // 3. 分配任务
        for (CollectTask task : sortedTasks) {
            try {
                // 选择执行节点
                NodeInfo targetNode = selectExecutionNode(
                        nodes, nodeTasks, task);
                if (targetNode == null) {
                    continue;
                }

                // 分配任务
                assignTaskToNode(task, targetNode);

                // 更新节点任务计数
                nodeTasks.merge(targetNode, 1, Integer::sum);

            } catch (Exception e) {
                log.error("Distribute task failed: {}", task.getId(), e);
            }
        }
    }

    /**
     * 选择执行节点
     */
    private NodeInfo selectExecutionNode(
            List<NodeInfo> nodes,
            Map<NodeInfo, Integer> nodeTasks,
            CollectTask task) {

        // 1. 过滤不可用节点
        List<NodeInfo> availableNodes = nodes.stream()
                .filter(node -> isNodeAvailable(node, task))
                .collect(Collectors.toList());

        if (availableNodes.isEmpty()) {
            return null;
        }

        // 2. 计算节点得分
        Map<NodeInfo, Double> nodeScores = availableNodes.stream()
                .collect(Collectors.toMap(
                        node -> node,
                        node -> calculateNodeScore(node, nodeTasks.get(node), task)
                ));

        // 3. 选择最优节点
        return nodeScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * 计算节点得分
     */
    private double calculateNodeScore(NodeInfo node,
                                      Integer taskCount, CollectTask task) {
        // 1. 基础分值
        double score = 1.0;

        // 2. 考虑CPU使用率
        score *= (1 - node.getCpuUsage() / 100.0);

        // 3. 考虑内存使用率
        score *= (1 - node.getMemoryUsage() / 100.0);

        // 4. 考虑任务数量
        score *= (1 - taskCount / 100.0);

        // 5. 考虑网络延迟
        score *= (1 - node.getNetworkLatency() / 1000.0);

        // 6. 考虑历史成功率
        score *= node.getSuccessRate();

        return score;
    }

    /**
     * 检查节点健康状态
     */
    private void checkNodeHealth() {
        try {
            // 1. 获取所有节点
            List<NodeInfo> allNodes = nodeManager.getAllNodes();

            // 2. 检查每个节点
            for (NodeInfo node : allNodes) {
                if (!isNodeHealthy(node)) {
                    handleUnhealthyNode(node);
                }
            }
        } catch (Exception e) {
            log.error("Check node health failed", e);
        }
    }

    /**
     * 处理不健康节点
     */
    private void handleUnhealthyNode(NodeInfo node) {
        try {
            log.warn("Node unhealthy: {}", node.getId());

            // 1. 获取节点任务
            List<CollectTask> nodeTasks =
                    taskRepository.findTasksByNodeId(node.getId());

            if (CollectionUtils.isEmpty(nodeTasks)) {
                return;
            }

            // 2. 迁移任务
            List<NodeInfo> healthyNodes = nodeManager.getActiveNodes().stream()
                    .filter(n -> !n.getId().equals(node.getId()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(healthyNodes)) {
                log.warn("No healthy nodes available for task migration");
                return;
            }

            // 3. 执行迁移
            migrateTasks(nodeTasks, healthyNodes);

        } catch (Exception e) {
            log.error("Handle unhealthy node failed: {}",
                    node.getId(), e);
        }
    }

    /**
     * 迁移任务
     */
    private void migrateTasks(List<CollectTask> tasks,
                              List<NodeInfo> targetNodes) {
        for (CollectTask task : tasks) {
            try {
                // 1. 选择目标节点
                NodeInfo targetNode = loadBalancer.selectNode(targetNodes);
                if (targetNode == null) {
                    continue;
                }

                // 2. 迁移任务
                migrateTask(task, targetNode);

            } catch (Exception e) {
                log.error("Migrate task failed: {}", task.getId(), e);
            }
        }
    }
}
