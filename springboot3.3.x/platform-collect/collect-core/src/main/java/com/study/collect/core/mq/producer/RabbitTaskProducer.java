package com.study.collect.core.mq.producer;

// RabbitMQ实现

import com.study.collect.core.mq.config.MQProperties;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.task.definition.TaskDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitTaskProducer implements TaskProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MQProperties properties;

    @Override
    public void sendTask(TaskDefinition task) {
        TaskMessage message = createTaskMessage(task);
        sendMessage(message);
        log.info("Send task message success, taskId: {}", task.getTaskId());
    }

    @Override
    public void sendShardingTask(TaskDefinition task, int shardingTotal) {
        for (int i = 0; i < shardingTotal; i++) {
            TaskMessage message = createTaskMessage(task);
            message.setShardingId(i);
            message.setShardingTotal(shardingTotal);
            sendMessage(message);
        }
        log.info("Send sharding task message success, taskId: {}, shardingTotal: {}",
                task.getTaskId(), shardingTotal);
    }

    @Override
    public void broadcastTask(TaskDefinition task) {
        TaskMessage message = createTaskMessage(task);
        sendMessage(message);
        log.info("Broadcast task message success, taskId: {}", task.getTaskId());
    }

    private TaskMessage createTaskMessage(TaskDefinition task) {
        TaskMessage message = new TaskMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setTaskId(task.getTaskId());
        message.setTaskDefinition(task);
        message.setNodeId(getNodeId());
        return message;
    }

    private void sendMessage(TaskMessage message) {
        MQProperties.RabbitMQ.Queue taskQueue = properties.getRabbit().getTask();
        rabbitTemplate.convertAndSend(
                taskQueue.getExchange(),
                taskQueue.getRoutingKey(),
                message
        );
    }

    private String getNodeId() {
        // TODO: 实现获取当前节点ID的逻辑
        return "NODE-" + UUID.randomUUID().toString().substring(0, 8);
    }
}

