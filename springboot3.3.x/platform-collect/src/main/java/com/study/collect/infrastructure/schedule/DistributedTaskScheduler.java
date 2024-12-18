package com.study.collect.infrastructure.schedule;

import com.study.collect.common.enums.collect.TaskStatus;
import com.study.collect.core.scheduler.base.AbstractTaskScheduler;
import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.domain.repository.task.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 分布式任务调度器
 */
@Slf4j
@Component("distributedScheduler")
@RequiredArgsConstructor
public class DistributedTaskScheduler extends AbstractTaskScheduler {

    private final TaskRepository taskRepository;
    private final LoadBalancer loadBalancer;
    private final NodeManager nodeManager;
    private final TaskLockManager lockManager;
    private final MetricsCollector metricsCollector;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    @Override
    public void start() {
        // 1. 注册当前节点
        nodeManager.register();

        // 2. 启动任务调度
        scheduler.scheduleWithFixedDelay(
                this::scheduleTasks,
                0,
                100,
                TimeUnit.MILLISECONDS
        );

        // 3. 启动节点健康检查
        scheduler.scheduleWithFixedDelay(
                this::checkNodesHealth,
                0,
                30,
                TimeUnit.SECONDS
        );
    }

    @Override
    public void stop() {
        try {
            // 1. 停止调度
            scheduler.shutdown();

            // 2. 迁移任务
            migrateTasks();

            // 3. 注销节点
            nodeManager.unregister();
        } catch (Exception e) {
            log.error("Stop scheduler failed", e);
        }
    }

    private void scheduleTasks() {
        try {
            // 1. 获取待执行的任务
            List<CollectTask> tasks = taskRepository
                    .findByStatus(TaskStatus.WAITING.name());

            if (CollectionUtils.isEmpty(tasks)) {
                return;
            }

            // 2. 获取可用节点
            List<String> availableNodes = nodeManager.getAvailableNodes();
            if (CollectionUtils.isEmpty(availableNodes)) {
                return;
            }

            // 3. 分发任务
            tasks.forEach(task -> dispatchTask(task, availableNodes));

        } catch (Exception e) {
            log.error("Schedule tasks failed", e);
        }
    }

    private void dispatchTask(CollectTask task, List<String> nodes) {
        try {
            // 1. 获取任务锁
            String lockKey = "task:lock:" + task.getId();
            if (!lockManager.tryLock(lockKey)) {
                return;
            }

            try {
                // 2. 选择执行节点
                String targetNode = loadBalancer.selectNode(nodes, task);
                if (targetNode == null) {
                    return;
                }

                // 3. 分配任务
                task.setNodeId(targetNode);
                task.setStatus(TaskStatus.ASSIGNED.name());
                task.setAssignTime(LocalDateTime.now());
                taskRepository.save(task);

                // 4. 发送任务消息
                sendTaskMessage(task);

                // 5. 更新指标
                metricsCollector.incrementAssignedTask(targetNode);

            } finally {
                lockManager.unlock(lockKey);
            }
        } catch (Exception e) {
            log.error("Dispatch task failed: {}", task.getId(), e);
        }
    }

    private void checkNodesHealth() {
        try {
            // 1. 获取所有节点
            List<NodeInfo> nodes = nodeManager.getAllNodes();

            // 2. 检查每个节点
            nodes.forEach(node -> {
                try {
                    // 2.1 检查心跳
                    if (isNodeDown(node)) {
                        handleNodeDown(node);
                    }

                    // 2.2 检查负载
                    if (isNodeOverloaded(node)) {
                        handleNodeOverload(node);
                    }
                } catch (Exception e) {
                    log.error("Check node health failed: {}", node.getId(), e);
                }
            });
        } catch (Exception e) {
            log.error("Check nodes health failed", e);
        }
    }

    private void migrateTasks() {
        try {
            // 1. 获取当前节点的任务
            String currentNode = nodeManager.getCurrentNodeId();
            List<CollectTask> tasks = taskRepository
                    .findByNodeId(currentNode);

            if (CollectionUtils.isEmpty(tasks)) {
                return;
            }

            // 2. 获取其他可用节点
            List<String> availableNodes = nodeManager.getAvailableNodes().stream()
                    .filter(node -> !node.equals(currentNode))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(availableNodes)) {
                log.warn("No available nodes for task migration");
                return;
            }

            // 3. 迁移任务
            tasks.forEach(task -> {
                try {
                    task.setStatus(TaskStatus.WAITING.name());
                    task.setNodeId(null);
                    taskRepository.save(task);
                } catch (Exception e) {
                    log.error("Migrate task failed: {}", task.getId(), e);
                }
            });

        } catch (Exception e) {
            log.error("Migrate tasks failed", e);
        }
    }
}