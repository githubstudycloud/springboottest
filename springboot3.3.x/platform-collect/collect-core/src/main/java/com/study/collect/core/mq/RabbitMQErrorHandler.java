package com.study.collect.core.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        log.error("RabbitMQ message processing error", t);

        if (t instanceof MessageConversionException) {
            // 消息转换错误处理
            log.error("Message conversion failed", t);
        } else if (t instanceof ListenerExecutionFailedException) {
            // 监听器执行错误处理
            log.error("Listener execution failed", t);
        }
    }
}