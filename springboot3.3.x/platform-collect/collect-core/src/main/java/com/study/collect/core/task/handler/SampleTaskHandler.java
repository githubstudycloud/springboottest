package com.study.collect.core.task.handler;

import com.study.collect.core.task.model.TaskContext;
import org.springframework.stereotype.Component;

@Component
public class SampleTaskHandler extends AbstractTaskHandler {

    @Override
    public String getType() {
        return "sample";
    }

    @Override
    protected Object doExecute(TaskContext context) {
        // 实现具体的任务处理逻辑
        return "Task executed successfully";
    }
}