package com.study.collect.infrastructure.mq.consumer;

import com.study.collect.domain.entity.task.CollectTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 消息消费者
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

    private final CollectTaskService taskService;

    @RabbitListener(queues = "collect.task.queue")
    public void processTask(CollectTask task, Channel channel, Message message) {
        try {
            log.info("Receive task message: {}", task.getId());
            taskService.processTask(task);
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Process task message failed", e);
            try {
                // 消息重试
                if (message.getMessageProperties().getRedelivered()) {
                    // 多次重试失败,放入死信队列
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                } else {
                    // 重新入队
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                }
            } catch (IOException ex) {
                log.error("Message retry failed", ex);
            }
        }
    }

    @RabbitListener(queues = "collect.result.queue")
    public void processResult(CollectResult result, Channel channel, Message message) {
        try {
            log.info("Receive result message: {}", result.getTaskId());
            taskService.processResult(result);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Process result message failed", e);
            try {
                if (message.getMessageProperties().getRedelivered()) {
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                } else {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                }
            } catch (IOException ex) {
                log.error("Message retry failed", ex);
            }
        }
    }
}
