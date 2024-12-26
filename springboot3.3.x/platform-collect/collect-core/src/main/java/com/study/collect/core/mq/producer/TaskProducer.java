package com.study.collect.core.mq.producer;

import com.study.collect.core.mq.config.MQProperties;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.message.TaskResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MQProperties mqProperties;

    @Autowired
    public TaskProducer(RabbitTemplate rabbitTemplate, MQProperties mqProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.mqProperties = mqProperties;
    }

//    public void sendTask(TaskMessage message) {
//        try {
//            MQProperties.RabbitMQ.Queue taskQueue = mqProperties.getRabbit().getTask();
//            rabbitTemplate.convertAndSend(
//                    taskQueue.getExchange(),
//                    taskQueue.getRoutingKey(),
//                    message
//            );
//            log.info("Task message sent: instanceId={}, taskCode={}, shard={}/{}",
//                    message.getInstanceId(),
//                    message.getTaskId(),
//                    message.getShardIndex() + 1,
//                    message.getShardTotal()
//            );
//        } catch (Exception e) {
//            log.error("Failed to send task message: " + message.getInstanceId(), e);
//            throw new RuntimeException("Message sending failed", e);
//        }
//    }

    public void sendTask(TaskMessage message) {
        try {
            MQProperties.RabbitMQ.Queue taskQueue = mqProperties.getRabbit().getTask();

            // 设置消息属性
            MessageProperties props = new MessageProperties();
            props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            props.getHeaders().put("__TypeId__", TaskMessage.class.getName());

            Message amqpMessage = rabbitTemplate.getMessageConverter()
                    .toMessage(message, props);

            rabbitTemplate.send(
                    taskQueue.getExchange(),
                    taskQueue.getRoutingKey(),
                    amqpMessage
            );

            log.info("Task message sent: instanceId={}, taskCode={}, shard={}/{}",
                    message.getInstanceId(),
                    message.getTaskId(),
                    message.getShardIndex() + 1,
                    message.getShardTotal()
            );
        } catch (Exception e) {
            log.error("Failed to send task message: " + message.getInstanceId(), e);
            throw new RuntimeException("Message sending failed", e);
        }
    }


    public void sendResult(TaskResultMessage message) {
        try {
            MQProperties.RabbitMQ.Queue resultQueue = mqProperties.getRabbit().getResult();
            rabbitTemplate.convertAndSend(
                    resultQueue.getExchange(),
                    resultQueue.getRoutingKey(),
                    message
            );
            log.info("Result message sent: instanceId={}, taskCode={}, success={}",
                    message.getInstanceId(),
                    message.getTaskId(),
                    message.getSuccess()
            );
        } catch (Exception e) {
            log.error("Failed to send result message: " + message.getInstanceId(), e);
            throw new RuntimeException("Message sending failed", e);
        }
    }
}