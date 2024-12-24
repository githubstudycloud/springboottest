package com.study.collect.core.mq.producer;

import com.study.collect.core.mq.config.RabbitProperties;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.task.definition.TaskDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RabbitTaskProducer implements TaskProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties properties;

    @Override
    public void sendTask(TaskDefinition task) {
        TaskMessage message = createTaskMessage(task);
        rabbitTemplate.convertAndSend(
                properties.getTask().getExchange(),
                properties.getTask().getRoutingKey(),
                message
        );
    }

    @Override
    public void sendShardingTask(TaskDefinition task, int shardingTotal) {
        for (int i = 0; i < shardingTotal; i++) {
            TaskMessage message = createTaskMessage(task);
            message.setShardingId(i);
            message.setShardingTotal(shardingTotal);

            rabbitTemplate.convertAndSend(
                    properties.getTask().getExchange(),
                    properties.getTask().getRoutingKey(),
                    message
            );
        }
    }

    @Override
    public void broadcastTask(TaskDefinition task) {
        rabbitTemplate.convertAndSend(
                properties.getTask().getExchange(),
                properties.getTask().getRoutingKey(),
                createTaskMessage(task)
        );
    }

    private TaskMessage createTaskMessage(TaskDefinition task) {
        TaskMessage message = new TaskMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setTaskId(task.getTaskId());
        message.setTaskDefinition(task);
        return message;
    }
}