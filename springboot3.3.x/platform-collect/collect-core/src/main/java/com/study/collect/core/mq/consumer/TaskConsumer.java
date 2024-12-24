package com.study.collect.core.mq.consumer;

// 消费者接口

import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.task.model.TaskContext;

public interface TaskConsumer {
    /**
     * 处理任务消息
     *
     * @param message 任务消息
     */
    void onMessage(TaskMessage message);

    /**
     * 判断是否为当前节点的分片
     *
     * @param message 任务消息
     * @return 是否处理
     */
    default boolean isCurrentShard(TaskMessage message) {
        return true;
    }

    /**
     * 构建任务上下文
     *
     * @param message 任务消息
     * @return 任务上下文
     */
    default TaskContext buildContext(TaskMessage message) {
        TaskContext context = new TaskContext();
        context.setTaskId(message.getTaskId());
        context.setShardingId(message.getShardingId());
        context.setShardingTotal(message.getShardingTotal());
        return context;
    }
}
