package com.study.collect.core.mq.message;

import com.study.collect.core.task.definition.TaskDefinition;
import lombok.Data;
import java.io.Serializable;
import java.util.Map;

@Data
public class TaskMessage implements Serializable {
    private String messageId;
    private String taskId;
    private String nodeId;
    private Integer shardingId;
    private Integer shardingTotal;
    private TaskDefinition taskDefinition;
    private Map<String,Object> context;
}
