package com.study.collect.infrastructure.mq.consumer;

import com.mysql.cj.protocol.Message;
import com.rabbitmq.client.Channel;
import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.model.response.collect.CollectResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 消息消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageConsumer {

    private final CollectTaskService taskService;
    private final RetryTemplate retryTemplate;

    /**
     * 消费任务消息
     */
    @RabbitListener(queues = "#{taskQueue.name}")
    public void consumeTask(CollectTask task, Channel channel, Message message) {
        String taskId = task.getId();
        log.info("Receive task message: {}", taskId);

        try {
            // 使用重试模板执行
            retryTemplate.execute(context -> {
                taskService.processTask(task);
                return null;
            });

            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("Process task success: {}", taskId);

        } catch (Exception e) {
            log.error("Process task failed: {}", taskId, e);
            handleTaskError(channel, message, e);
        }
    }
    /**
     * 消费结果消息
     */
    @RabbitListener(queues = "#{resultQueue.name}")
    public void consumeResult(CollectResult result, Channel channel, Message message) {
        String taskId = result.getTaskId();
        log.info("Receive result message: {}", taskId);

        try {
            taskService.processResult(result);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("Process result success: {}", taskId);

        } catch (Exception e) {
            log.error("Process result failed: {}", taskId, e);
            handleResultError(channel, message, e);
        }
    }

    /**
     * 处理任务错误
     */
    private void handleTaskError(Channel channel, Message message, Exception e) throws IOException {
        if (message.getMessageProperties().getRedelivered()) {
            // 多次重试失败，放入死信队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        } else {
            // 重新入队
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    /**
     * 处理结果错误
     */
    private void handleResultError(Channel channel, Message message, Exception e) throws IOException {
        // 结果处理失败直接丢弃
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
    }
}