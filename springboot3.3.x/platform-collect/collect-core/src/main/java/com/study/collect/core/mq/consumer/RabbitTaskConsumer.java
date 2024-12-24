package com.study.collect.core.mq.consumer;

// RabbitMQ实现

import com.study.collect.core.mq.config.MQProperties;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.task.executor.TaskExecutor;
import com.study.collect.core.task.model.TaskContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitTaskConsumer implements TaskConsumer {

    private final TaskExecutor taskExecutor;
    private final MQProperties properties;

    @Override
    @RabbitListener(queues = "#{@taskQueue.name}")
    public void onMessage(TaskMessage message) {
        try {
            log.info("Receive task message: {}", message);

            // 1. 判断是否是当前节点的分片
            if (!isCurrentShard(message)) {
                log.info("Not current shard task, ignore, taskId: {}, shardingId: {}",
                        message.getTaskId(), message.getShardingId());
                return;
            }

            // 2. 构建任务上下文
            TaskContext context = buildContext(message);

            // 3. 执行任务
            taskExecutor.execute(message.getTaskDefinition(), context);

            log.info("Process task message success, taskId: {}", message.getTaskId());
        } catch (Exception e) {
            log.error("Process task message failed, taskId: {}", message.getTaskId(), e);
            // TODO: 异常处理、重试、死信队列等逻辑
        }
    }

    @Override
    public boolean isCurrentShard(TaskMessage message) {
        // TODO: 实现分片判断逻辑
        return message.getShardingId() == null ||
                message.getShardingId().equals(getCurrentShardingId());
    }

    private Integer getCurrentShardingId() {
        // TODO: 实现获取当前节点分片ID的逻辑
        return 0;
    }
}
