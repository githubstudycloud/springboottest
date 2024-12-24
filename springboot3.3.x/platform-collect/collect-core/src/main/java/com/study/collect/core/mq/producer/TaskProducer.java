package com.study.collect.core.mq.producer;

//生产者接口

import com.study.collect.core.task.definition.TaskDefinition;

public interface TaskProducer {
    /**
     * 发送任务消息
     *
     * @param task 任务定义
     */
    void sendTask(TaskDefinition task);

    /**
     * 发送分片任务消息
     *
     * @param task          任务定义
     * @param shardingTotal 分片总数
     */
    void sendShardingTask(TaskDefinition task, int shardingTotal);

    /**
     * 广播任务消息
     *
     * @param task 任务定义
     */
    void broadcastTask(TaskDefinition task);
}