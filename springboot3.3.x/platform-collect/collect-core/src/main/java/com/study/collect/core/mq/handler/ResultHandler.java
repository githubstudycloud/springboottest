package com.study.collect.core.mq.handler;

import com.study.collect.core.mq.message.ResultMessage;
import com.study.collect.core.mq.producer.ResultProducer;
import com.study.collect.core.task.model.TaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 结果处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResultHandler {

    private final ResultProducer resultProducer;

    public void handleTaskResult(String taskId, TaskResult result) {
        log.info("开始处理任务执行结果: taskId={}", taskId);

        try {
            ResultMessage message = createResultMessage(taskId, result);
            resultProducer.sendResult(message);
            log.info("任务执行结果处理完成: taskId={}", taskId);

        } catch (Exception e) {
            log.error("任务执行结果处理失败: taskId={}", taskId, e);
        }
    }

    private ResultMessage createResultMessage(String taskId, TaskResult result) {
        ResultMessage message = new ResultMessage();
        message.setTaskId(taskId);
        message.setSuccess(result.getSuccess());
        message.setResult(result.getData());

        if (!result.getSuccess()) {
            message.setErrorMsg(result.getErrorMessage());
        }

        return message;
    }
}
