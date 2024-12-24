package com.study.collect.core.mq.consumer;

import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.task.TaskContext;
import com.study.collect.core.task.executor.TaskExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskConsumer {

    private final TaskExecutor taskExecutor;

    @RabbitListener(queues = "${collect.mq.rabbit.task.queue}")
    public void onMessage(TaskMessage message) {
        try {
            log.info("Receive task message: {}", message);

            // 1. 构建任务上下文
            TaskContext context = new TaskContext();
            context.setTaskId(message.getTaskId());
            context.setShardingId(message.getShardingId());
            context.setShardingTotal(message.getShardingTotal());
            context.setParams(message.getContext());

            // 2. 执行任务
            taskExecutor.execute(message.getTaskDefinition(), context);

        } catch (Exception e) {
            log.error("Process task message failed", e);
            // 异常处理
        }
    }
}