package com.study.collect.core.task.executor;

// 异步执行器

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.model.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncTaskExecutor extends AbstractTaskExecutor {

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final DefaultTaskExecutor defaultTaskExecutor;

    public AsyncTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor,
                             DefaultTaskExecutor defaultTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.defaultTaskExecutor = defaultTaskExecutor;
    }

    @Override
    protected void doExecute(TaskDefinition task, TaskContext context) {
        threadPoolTaskExecutor.execute(() -> {
            try {
                defaultTaskExecutor.execute(task, context);
            } catch (Exception e) {
                log.error("Async task execution failed, taskId: {}",
                        task.getTaskId(), e);
            }
        });
    }

    @Override
    protected void beforeExecute(TaskDefinition task, TaskContext context) {
        super.beforeExecute(task, context);
        log.info("Submit async task, taskId: {}, active threads: {}",
                task.getTaskId(), threadPoolTaskExecutor.getActiveCount());
    }
}