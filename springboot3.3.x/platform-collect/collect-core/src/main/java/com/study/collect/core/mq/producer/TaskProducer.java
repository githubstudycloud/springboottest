package com.study.collect.core.mq.producer;

import com.study.collect.core.task.CollectTask;
import com.study.collect.core.task.TaskResult;
import com.study.collect.core.task.TaskResultHandler;
import com.study.collect.core.task.TaskStatus;
import com.study.collect.core.task.splitter.TaskSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// 1. 任务生产者
@Component
@RequiredArgsConstructor
public class TaskProducer {

    private final RabbitTemplate rabbitTemplate;
    private final TaskSplitter taskSplitter;

    @Value("${mq.task.exchange}")
    private String taskExchange;

    @Value("${mq.task.routing-key}")
    private String taskRoutingKey;

    public void sendTask(CollectTask task, int shardCount) {
        // 1. 任务分片
        List<CollectTask> tasks = taskSplitter.split(task, shardCount);

        // 2. 发送任务
        tasks.forEach(subTask -> {
            rabbitTemplate.convertAndSend(taskExchange, taskRoutingKey, subTask);
        });
    }
}


}

