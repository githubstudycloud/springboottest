package com.study.collect.core.mq.message;

// 结果消息

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResultMessage extends BaseMessage {
    private String taskId;
    private String nodeId;
    private Boolean success;
    private String errorMsg;
    private Object result;
    private LocalDateTime finishTime;

    public ResultMessage() {
        super();
        setType("RESULT");
        this.finishTime = LocalDateTime.now();
    }
}
