package com.study.collect.core.mq.message;

import com.study.collect.core.task.definition.TaskDefinition;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskMessage extends BaseMessage {
    private String taskId;
    private String nodeId;
    private Integer shardingId;
    private Integer shardingTotal;
    private TaskDefinition taskDefinition;

    public TaskMessage() {
        super();
        setType("TASK");
    }
}