package com.study.collect.core.mq.producer;

import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.task.CollectTask;
import com.study.collect.core.task.TaskResult;
import com.study.collect.core.task.TaskResultHandler;
import com.study.collect.core.task.TaskStatus;
import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.splitter.TaskSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// 任务生产者
public interface TaskProducer {
    // 发送任务消息
    void sendTask(TaskDefinition task);

    // 发送带分片的任务消息
    void sendShardingTask(TaskDefinition task, int shardingTotal);

    // 广播任务消息
    void broadcastTask(TaskDefinition task);
}

