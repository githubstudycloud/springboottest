package com.study.collect.core.task.handler;

import com.study.collect.core.task.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CollectTaskHandler implements TaskHandler {

    @Override
    public void handle(TaskContext context) {
        log.info("Execute collect task, context: {}", context);
        // 具体采集逻辑由业务模块实现
    }
}