package com.study.collect.core.mq.message;

// 基础消息

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class BaseMessage implements Serializable {
    private String messageId;
    private String type;
    private LocalDateTime createTime;

    public BaseMessage() {
        this.createTime = LocalDateTime.now();
    }

    protected void setType(String type) {
        this.type = type;
    }
}