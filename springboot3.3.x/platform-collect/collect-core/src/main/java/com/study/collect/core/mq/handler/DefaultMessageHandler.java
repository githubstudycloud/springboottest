package com.study.collect.core.mq.handler;

import com.study.collect.core.mq.message.ResultMessage;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.task.executor.TaskExecutor;
import com.study.collect.core.task.model.TaskContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 默认消息处理器实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultMessageHandler implements MessageHandler {

    private final TaskExecutor taskExecutor;

    @Override
    public void handleTaskMessage(TaskMessage message) {
        log.info("开始处理任务消息: taskId={}", message.getTaskId());

        try {
            TaskContext context = buildTaskContext(message);
            taskExecutor.execute(message.getTaskDefinition(), context);
            log.info("任务消息处理完成: taskId={}", message.getTaskId());

        } catch (Exception e) {
            log.error("任务消息处理失败: taskId={}", message.getTaskId(), e);
            handleTaskError(message, e);
        }
    }

    @Override
    public void handleResultMessage(ResultMessage message) {
        log.info("开始处理结果消息: taskId={}, success={}", message.getTaskId(), message.getSuccess());

        try {
            if (message.getSuccess()) {
                handleTaskSuccess(message);
            } else {
                handleTaskFailure(message);
            }
            log.info("结果消息处理完成: taskId={}", message.getTaskId());

        } catch (Exception e) {
            log.error("结果消息处理失败: taskId={}", message.getTaskId(), e);
        }
    }

    private TaskContext buildTaskContext(TaskMessage message) {
        TaskContext context = new TaskContext();
        context.setTaskId(message.getTaskId());
        context.setShardingId(message.getShardingId());
        context.setShardingTotal(message.getShardingTotal());
        return context;
    }

    private void handleTaskError(TaskMessage message, Exception e) {
        // 任务执行异常处理逻辑
    }

    private void handleTaskSuccess(ResultMessage message) {
        // 任务执行成功处理逻辑
    }

    private void handleTaskFailure(ResultMessage message) {
        // 任务执行失败处理逻辑
    }
}
