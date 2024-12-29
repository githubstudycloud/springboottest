package com.study.collect.core.mq.message;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class TaskMessage extends BaseMessage {
    private String taskId;           // 任务编码
    private String instanceId;       // 实例ID
    private Integer shardIndex;      // 分片索引
    private Integer shardTotal;      // 分片总数
    private String shardParam;       // 分片参数
    private String hostName;         // 执行机器

    public TaskMessage() {
        super();
        setType("TASK");
    }
}