package com.study.collect.core.mq.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component
public class DeFaultMessageConverter implements MessageConverter {

    private final ObjectMapper objectMapper;

    public DeFaultMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Message toMessage(Object object, MessageProperties properties) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(object);
            return new Message(bytes, properties);
        } catch (Exception e) {
            throw new RuntimeException("Convert to message failed", e);
        }
    }

    @Override
    public Object fromMessage(Message message) {
        try {
            Class<?> type = Class.forName(message.getMessageProperties().getContentType());
            return objectMapper.readValue(message.getBody(), type);
        } catch (Exception e) {
            throw new RuntimeException("Convert from message failed", e);
        }
    }
}