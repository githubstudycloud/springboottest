package com.study.collect.core.task.executor;

// 默认执行器

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.handler.TaskHandler;
import com.study.collect.core.task.model.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DefaultTaskExecutor extends AbstractTaskExecutor {

    private final Map<String, TaskHandler> taskHandlers;

    public DefaultTaskExecutor(Map<String, TaskHandler> taskHandlers) {
        this.taskHandlers = taskHandlers;
    }

    @Override
    protected void doExecute(TaskDefinition task, TaskContext context) {
        // 1. 获取任务处理器
        TaskHandler handler = getTaskHandler(task);

        // 2. 执行任务处理
        handler.handle(context);
    }

    private TaskHandler getTaskHandler(TaskDefinition task) {
        TaskHandler handler = taskHandlers.get(task.getTaskHandler());
        if (handler == null) {
            throw new IllegalStateException(
                    "Task handler not found: " + task.getTaskHandler());
        }
        return handler;
    }
}
