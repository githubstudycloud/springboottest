// MessageConverter.java
package com.study.collect.core.mq.message.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component
public class DefaultMessageConverter implements MessageConverter {

    private final ObjectMapper objectMapper;

    public DefaultMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Message toMessage(Object object, MessageProperties properties) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(object);
            properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            properties.setContentEncoding("UTF-8");
            return new Message(bytes, properties);
        } catch (Exception e) {
            throw new RuntimeException("消息转换失败", e);
        }
    }

    @Override
    public Object fromMessage(Message message) {
        try {
            String contentType = message.getMessageProperties().getContentType();
            if (contentType != null && contentType.contains("json")) {
                return objectMapper.readValue(message.getBody(), Object.class);
            }
            return message.getBody();
        } catch (Exception e) {
            throw new RuntimeException("消息反序列化失败", e);
        }
    }
}
