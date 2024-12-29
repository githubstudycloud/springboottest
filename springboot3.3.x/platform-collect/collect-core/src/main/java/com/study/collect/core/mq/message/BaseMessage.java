package com.study.collect.core.mq.message;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 消息基类
 * 定义所有消息共有的属性和行为
 */
@Data
public abstract class BaseMessage implements Serializable {

    /**
     * 消息ID，用于消息追踪
     */
    private String messageId;

    /**
     * 消息类型，用于区分不同消息
     */
    private String type;

    /**
     * 消息创建时间
     */
    private LocalDateTime createTime;

    /**
     * 消息优先级
     */
    private Integer priority;

    /**
     * 消息重试次数
     */
    private Integer retryCount;

    /**
     * 额外属性，用于扩展
     */
    private String properties;

    public BaseMessage() {
        this.messageId = UUID.randomUUID().toString();
        this.createTime = LocalDateTime.now();
        this.retryCount = 0;
    }

    /**
     * 设置消息类型
     * 子类需要在构造函数中调用此方法设置具体的消息类型
     */
    protected void setType(String type) {
        this.type = type;
    }

    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
        this.retryCount++;
    }
}