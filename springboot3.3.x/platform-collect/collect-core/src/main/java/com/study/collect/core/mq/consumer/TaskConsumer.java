package com.study.collect.core.mq.consumer;


import com.study.collect.core.collector.ICollector;
import com.study.collect.core.task.CollectTask;
import com.study.collect.core.task.TaskResult;
import com.study.collect.core.task.TaskResultHandler;
import com.study.collect.core.task.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

// 2. 任务消费者
@Component
@RequiredArgsConstructor
@Slf4j
public class TaskConsumer {

    private final Map<String, ICollector> collectors;
    private final TaskResultHandler resultHandler;

    @RabbitListener(queues = "${mq.task.queue}")
    public void handleTask(CollectTask task) {
        try {
            // 1. 获取对应的采集器
            ICollector collector = collectors.get(task.getType());
            if (collector == null) {
                throw new IllegalArgumentException("Unknown task type: " + task.getType());
            }

            // 2. 执行采集
            task.setStatus(TaskStatus.RUNNING);
            Object result = collector.collect(task.getParams());

            // 3. 处理结果
            TaskResult taskResult = new TaskResult();
            taskResult.setTaskId(task.getId());
            taskResult.setType(task.getType());
            taskResult.setSuccess(true);
            taskResult.setData(result);
            taskResult.setFinishTime(LocalDateTime.now());

            resultHandler.handleResult(taskResult);

        } catch (Exception e) {
            log.error("Task execution failed: " + task.getId(), e);

            // 4. 处理异常
            TaskResult taskResult = new TaskResult();
            taskResult.setTaskId(task.getId());
            taskResult.setType(task.getType());
            taskResult.setSuccess(false);
            taskResult.setMessage(e.getMessage());
            taskResult.setFinishTime(LocalDateTime.now());

            resultHandler.handleResult(taskResult);
        }
    }