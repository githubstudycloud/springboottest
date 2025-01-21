package com.study.collect.core.mq.consumer;

import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.message.TaskResultMessage;
import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.handler.TaskHandler;
import com.study.collect.core.task.handler.TaskHandlerManager;
import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskResult;
import com.study.collect.core.task.service.TaskExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class TaskConsumer {

    private final TaskExecuteService taskExecuteService;
    private final TaskProducer taskProducer;
    private final TaskHandlerManager handlerManager;

    @Autowired
    public TaskConsumer(TaskExecuteService taskExecuteService,
                        TaskProducer taskProducer,
                        TaskHandlerManager handlerManager) {
        this.taskExecuteService = taskExecuteService;
        this.taskProducer = taskProducer;
        this.handlerManager = handlerManager;
    }

    @RabbitListener(
            queues = "${collect.mq.rabbit.task.queue}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onTaskMessage(@Payload TaskMessage message) {
        String instanceId = message.getInstanceId();
        try {
            log.info("Received task message: instanceId={}, taskCode={}, shard={}/{}",
                    message.getInstanceId(),
                    message.getTaskId(),
                    message.getShardIndex() + 1,
                    message.getShardTotal()
            );

//    @RabbitListener(queues = "${collect.mq.rabbit.task.queue}")
//    public void onTaskMessage(TaskMessage message) {

//        log.info("Received task message: instanceId={}, taskCode={}, shard={}/{}",
//                instanceId,
//                message.getTaskId(),
//                message.getShardIndex() + 1,
//                message.getShardTotal()
//        );
//
//        try {
            // 执行任务
            Object result = executeTask(message);

            // 发送成功结果
            sendSuccessResult(message, result);

            // 更新任务状态
            taskExecuteService.completeTaskInstance(instanceId, true, null);

            log.info("Task executed successfully: {}", instanceId);

        } catch (Exception e) {
            log.error("Task execution failed: " + instanceId, e);

            // 发送失败结果
            sendFailureResult(message, e.getMessage());

            // 更新任务状态
            taskExecuteService.completeTaskInstance(instanceId, false, e.getMessage());
        }
    }

    //    private Object executeTask(TaskMessage message) {
//        // 实际任务执行逻辑
//        // 这里需要根据具体业务实现，可能需要调用具体的TaskHandler
//        return null;
//    }
    private Object executeTask(TaskMessage message) {
        // 创建任务上下文
        TaskContext context = createTaskContext(message);

        // 获取任务处理器
        TaskHandler handler = handlerManager.getHandler(message.getTaskId());

        // 执行任务
        TaskResult result = handler.execute(context);

        if (result.getSuccess()) {
            // 发送成功结果
            sendSuccessResult(message, result.getData());
        } else {
            // 发送失败结果
            sendFailureResult(message, result.getErrorMessage());
            throw new RuntimeException(result.getErrorMessage());
        }


        return result.getData();
    }

    private TaskContext createTaskContext(TaskMessage message) {
        TaskContext context = new TaskContext();
        context.setTaskId(message.getTaskId());
        context.setInstanceId(message.getInstanceId());
        context.setShardIndex(message.getShardIndex());
        context.setShardTotal(message.getShardTotal());
        context.setShardParam(message.getShardParam());
        return context;
    }

    private void sendSuccessResult(TaskMessage message, Object result) {
        TaskResultMessage resultMessage = createResultMessage(message);
        resultMessage.setSuccess(true);
        resultMessage.setResult(result);
        taskProducer.sendResult(resultMessage);
    }

    private void sendFailureResult(TaskMessage message, String errorMsg) {
        TaskResultMessage resultMessage = createResultMessage(message);
        resultMessage.setSuccess(false);
        resultMessage.setErrorMsg(errorMsg);
        taskProducer.sendResult(resultMessage);
    }

    private TaskResultMessage createResultMessage(TaskMessage message) {
        TaskResultMessage resultMessage = new TaskResultMessage();
        resultMessage.setTaskId(message.getTaskId());
        resultMessage.setInstanceId(message.getInstanceId());
        resultMessage.setExecuteHost(message.getHostName());
        resultMessage.setFinishTime(LocalDateTime.now());
        return resultMessage;
    }
}