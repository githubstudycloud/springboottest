package com.study.collect.core.task;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// 3. 结果处理器
@Component
@RequiredArgsConstructor
public class TaskResultHandler {

    private final RabbitTemplate rabbitTemplate;

    @Value("${mq.result.exchange}")
    private String resultExchange;

    @Value("${mq.result.routing-key}")
    private String resultRoutingKey;

    public void handleResult(TaskResult result) {
        // 发送结果到结果队列
        rabbitTemplate.convertAndSend(resultExchange, resultRoutingKey, result);
    }
}
