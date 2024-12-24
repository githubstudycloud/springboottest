package com.study.collect.core.mq.consumer;

import com.study.collect.core.mq.message.TaskMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "${collect.mq.rabbit.task.queue}")
@RequiredArgsConstructor
public class RabbitTaskConsumer implements TaskConsumer {

    private final TaskExecutor taskExecutor;

    @Override
    public void onMessage(TaskMessage message) {
        // 1. 判断是否是本节点的分片
        if (!isCurrentShard(message)) {
            return;
        }

        // 2. 执行任务
        TaskContext context = buildContext(message);
        taskExecutor.execute(message.getTask(), context);
    }

    private boolean isCurrentShard(TaskMessage message) {
        return message.getShardingId() == null ||
                message.getShardingId().equals(getShardingId());
    }
}
