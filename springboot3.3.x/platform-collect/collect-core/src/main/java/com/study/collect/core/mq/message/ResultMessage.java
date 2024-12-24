package com.study.collect.core.mq.message;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ResultMessage implements Serializable {
    private String messageId;
    private String taskId;
    private String nodeId;
    private Boolean success;
    private String errorMsg;
    private Object result;
    private LocalDateTime finishTime;
}