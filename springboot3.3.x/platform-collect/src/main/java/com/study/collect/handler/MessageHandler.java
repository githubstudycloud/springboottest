package com.study.collect.handler;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageHandler implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            // 获取消息ID
            long deliveryTag = message.getMessageProperties().getDeliveryTag();

            // 获取消息内容
            String msg = new String(message.getBody());
            log.info("Received message: {}", msg);

            // 处理消息
            processMessage(msg);

            // 手动确认消息
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Error processing message", e);
            // 消息处理失败，拒绝消息并重新入队
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    private void processMessage(String message) {
        // 实现具体的消息处理逻辑
    }
}