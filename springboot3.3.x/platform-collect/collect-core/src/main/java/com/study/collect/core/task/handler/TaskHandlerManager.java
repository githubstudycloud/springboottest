package com.study.collect.core.task.handler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TaskHandlerManager {

//    private final Map<String, TaskHandler> handlerMap = new HashMap<>();

    @Autowired
    private List<TaskHandler> handlers;

    @PostConstruct
    public void init() {
        handlers.forEach(handler -> handlerMap.put(handler.getType(), handler));
    }

//    public TaskHandler getHandler(String type) {
//        TaskHandler handler = handlerMap.get(type);
//        if (handler == null) {
//            throw new IllegalArgumentException("未找到任务处理器: " + type);
//        }
//        return handler;
//    }

    private final Map<String, TaskHandler> handlerMap = new HashMap<>();

    @Autowired
    public TaskHandlerManager(List<TaskHandler> handlers) {
        handlers.forEach(handler -> {
            handlerMap.put(handler.getType(), handler);
            log.info("注册任务处理器: {}", handler.getType());
        });
    }

    public TaskHandler getHandler(String type) {
        TaskHandler handler = handlerMap.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("未找到任务处理器: " + type);
        }
        return handler;
    }
}
