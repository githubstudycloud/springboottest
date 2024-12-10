package com.study.collect.service;

import com.study.collect.core.collector.Collector;
import com.study.collect.core.strategy.CollectorStrategy;
import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TaskResult;
import com.study.collect.enums.CollectorType;
import com.study.collect.enums.TaskStatus;
import com.study.collect.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskManagementService {

    private static final Logger logger = LoggerFactory.getLogger(TaskManagementService.class);

    private final CollectorStrategy collectorStrategy;
    private final TaskRepository taskRepository;
    private final ApplicationContext applicationContext;
    private final Map<String, Collector> collectorMap;

    // 使用构造函数注入替代@Autowired
    public TaskManagementService(CollectorStrategy collectorStrategy,
                                 TaskRepository taskRepository,
                                 ApplicationContext applicationContext,
                                 Map<String, Collector> collectorMap) {
        this.collectorStrategy = collectorStrategy;
        this.taskRepository = taskRepository;
        this.applicationContext = applicationContext;
        this.collectorMap = collectorMap;
    }

    // 添加启动时的测试代码
    @PostConstruct
    public void init() {
        logger.info("=== Testing Collector Registration ===");
        // 输出所有注册的CollectorType
        logger.info("Available Collector Types: {}", Arrays.toString(CollectorType.values()));

        // 尝试通过Spring Context获取所有Collector
        Map<String, Collector> collectors = applicationContext.getBeansOfType(Collector.class);
        logger.info("Registered Collectors in Spring Context: {}", collectors.keySet());

        // 测试每个类型的collector是否可用
        for (CollectorType type : CollectorType.values()) {
            try {
                Collector collector = getCollectorForType(type);
                logger.info("Collector for type {}: {}", type, collector);
            } catch (Exception e) {
                logger.error("Error getting collector for type {}: {}", type, e.getMessage());
            }
        }
        logger.info("=== Collector Registration Test Complete ===");
    }

    private Collector getCollectorForType(CollectorType type) {
        // 首先尝试从Spring容器获取
        try {
            String beanName = type.name().toLowerCase() + "Collector";
            logger.debug("Trying to get collector bean: {}", beanName);
            if (applicationContext.containsBean(beanName)) {
                return applicationContext.getBean(beanName, Collector.class);
            }
        } catch (Exception e) {
            logger.debug("Could not get collector from Spring context: {}", e.getMessage());
        }

        // 然后尝试从CollectorStrategy获取
        Collector collector = collectorStrategy.getCollector(type);
        if (collector != null) {
            return collector;
        }

        throw new IllegalStateException("No collector found for type: " + type);
    }

    public TaskResult executeTask(String taskId) {
        // 1. 获取并验证任务
        Optional<CollectTask> taskOpt = getTask(taskId);
        if (taskOpt.isEmpty()) {
            logger.warn("Task not found with ID: {}", taskId);
//            return buildFailedResult(taskId, "Task not found");
            return TaskResult.builder()
                    .taskId(taskId)
                    .status(TaskStatus.FAILED)
                    .message("Task not found")
                    .build();
        }
// 2. 任务状态预检查
        CollectTask task = taskOpt.get();
        TaskResult validationResult = validateTaskExecution(task);
        if (validationResult != null) {
            return validationResult;
        }

        try {
            // 3. 更新任务为执行中状态
            updateTaskStatus(task, TaskStatus.RUNNING);

            // 4. 根据任务类型获取对应的采集器并执行
            // 使用新的获取collector方法
            Collector collector;
            try {
                collector = getCollectorForType(task.getCollectorType());
                logger.info("Found collector {} for type {}", collector.getClass().getSimpleName(),
                        task.getCollectorType());
            } catch (Exception e) {
                logger.error("Failed to get collector: {}", e.getMessage());
                throw new IllegalStateException("No collector found for type: " + task.getCollectorType());
            }

            TaskResult result = collector.collect(task);

            // 3. 根据执行结果更新最终状态
            // 更新状态和处理结果
            TaskStatus finalStatus = result.isSuccess() ? TaskStatus.COMPLETED : TaskStatus.FAILED;
            updateTaskStatus(task, finalStatus);
            handleTaskResult(task, result);

            return result;

        } catch (Exception e) {
            logger.error("Error executing task {}: {}", taskId, e.getMessage(), e);
            updateTaskStatus(task, TaskStatus.FAILED);
            handleTaskError(task, e);
            return buildFailedResult(taskId, "Task execution failed: " + e.getMessage());
        }
    }


    private void handleTaskError(CollectTask task, Exception e) {
        // 更新任务状态和错误信息
        task.setStatus(TaskStatus.FAILED);
        task.setUpdateTime(new Date());
        task.setRetryCount(task.getRetryCount() + 1);
        task.setLastError(e.getMessage());

        // 检查重试次数
        if (task.getRetryCount() >= task.getMaxRetries()) {
            logger.error("Task {} failed with max retries exceeded. Last error: {}",
                    task.getId(), e.getMessage());
            task.setStatus(TaskStatus.FAILED);
        } else {
            logger.warn("Task {} failed with error: {}. Retry count: {}/{}",
                    task.getId(), e.getMessage(), task.getRetryCount(), task.getMaxRetries());
        }

        try {
            taskRepository.save(task);
        } catch (Exception saveException) {
            logger.error("Failed to save task error state for task {}: {}",
                    task.getId(), saveException.getMessage(), saveException);
        }

        // 可能需要触发告警或通知
        notifyTaskError(task, e);
    }

    private void notifyTaskError(CollectTask task, Exception e) {
        try {
            // TODO: 实现错误通知逻辑，比如发送邮件或消息
            // 这里先用日志记录，后续可以扩展
            logger.error("Task execution failed - ID: {}, Type: {}, Error: {}",
                    task.getId(),
                    task.getCollectorType(),
                    e.getMessage());
        } catch (Exception notifyException) {
            logger.error("Failed to send error notification for task {}: {}",
                    task.getId(), notifyException.getMessage(), notifyException);
        }
    }

    private TaskResult validateTaskExecution(CollectTask task) {
        // 检查任务状态,使用equals避免NPE
        if (TaskStatus.RUNNING.equals(task.getStatus())) {
            logger.warn("Task {} is already running", task.getId());
            return buildFailedResult(task.getId(), "Task is already running");
        }

        // 检查重试次数
        if (task.getRetryCount() >= task.getMaxRetries()) {
            logger.warn("Task {} has exceeded max retry count", task.getId());
            return buildFailedResult(task.getId(), "Max retries exceeded");
        }

        return null;
    }

    private void handleTaskResult(CollectTask task, TaskResult result) {
        task.setStatus(result.getStatus());
        task.setUpdateTime(new Date());
        if (TaskStatus.FAILED.equals(result.getStatus())) {
            task.setRetryCount(task.getRetryCount() + 1);
            task.setLastError(result.getMessage());
        }
        taskRepository.save(task);

        logger.info("Task {} completed with status: {}", task.getId(), result.getStatus());
    }

    public CollectTask createTask(CollectTask task) {
        task.setStatus(TaskStatus.CREATED);
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        task.setRetryCount(0); // 初始化重试次数

        CollectTask savedTask = taskRepository.save(task);
        logger.info("Created new task with ID: {}", savedTask.getId());

        return savedTask;
    }

    public List<CollectTask> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public Optional<CollectTask> getTask(String taskId) {
        return taskRepository.findById(taskId);
    }

    public void stopTask(String taskId) {
        Optional<CollectTask> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            CollectTask task = taskOpt.get();
            // 使用对应类型的collector
            Collector collector = collectorMap.get(task.getCollectorType());
            if (collector != null) {
                collector.stopTask(taskId);
            }

            updateTaskStatus(task, TaskStatus.STOPPED);
            logger.info("Stopped task: {}", taskId);
        }
    }

    private void updateTaskStatus(CollectTask task, TaskStatus status) {
        task.setStatus(status);
        task.setUpdateTime(new Date());
        taskRepository.save(task);
        logger.info("Updated task {} status to: {}", task.getId(), status);
    }

    private TaskResult buildFailedResult(String taskId, String message) {
        return TaskResult.builder()
                .taskId(taskId)
                .status(TaskStatus.FAILED)
                .message(message)
                .build();
    }

    public TaskResult retryTask(String taskId) {
        Optional<CollectTask> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return TaskResult.builder()
                    .taskId(taskId)
                    .status(TaskStatus.FAILED)
                    .message("Task not found")
                    .build();
        }

        CollectTask task = taskOpt.get();
        if (task.getRetryCount() >= task.getMaxRetries()) {
            return TaskResult.builder()
                    .taskId(taskId)
                    .status(TaskStatus.FAILED)
                    .message("Max retries exceeded")
                    .build();
        }

        task.setRetryCount(task.getRetryCount() + 1);
        task.setUpdateTime(new Date());
        taskRepository.save(task);

        return executeTask(taskId);
    }
}