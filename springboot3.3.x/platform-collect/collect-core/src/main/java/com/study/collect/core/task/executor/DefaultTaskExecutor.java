package com.study.collect.core.task.executor;

import com.study.collect.core.task.TaskContext;
import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.handler.TaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DefaultTaskExecutor extends AbstractTaskExecutor {

    private final Map<String, TaskHandler> taskHandlers;

    @Override
    protected void doExecute(TaskDefinition task, TaskContext context) {
        // 1. 获取任务处理器
        TaskHandler handler = taskHandlers.get(task.getTaskHandler());
        if (handler == null) {
            throw new RuntimeException("Task handler not found: " + task.getTaskHandler());
        }

        // 2. 执行任务处理
        handler.handle(context);
    }
}