package com.study.collect.business.enterprise.service;

import com.study.collect.business.enterprise.collector.EnterpriseCollector;
import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.business.enterprise.processor.EnterpriseProcessor;
import com.study.collect.business.enterprise.repository.EnterpriseRepository;
import com.study.collect.core.mq.producer.TaskProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseCollector collector;
    private final EnterpriseProcessor processor;
    private final EnterpriseRepository repository;

    public Enterprise collectAndProcess(String code) {
        // 1. 采集数据
        Enterprise enterprise = collector.collect(code);
        if (enterprise == null) {
            return null;
        }

        // 2. 处理数据
        enterprise = processor.process(enterprise);

        // 3. 保存数据
        return repository.save(enterprise);
    }

    @Autowired
    private TaskProducer taskProducer;

    // 大批量数据采集
    public void batchCollect(List<String> codes) {
        // 创建采集任务
        CollectTask task = CollectTask.builder()
                .type("enterprise")
                .params(codes)
                .build();

        // 发送到消息队列
        taskProducer.sendTask(task);
    }

    // 结果处理
    @RabbitListener(queues = "#{taskResultQueue.name}")
    public void handleResult(TaskResult result) {
        // 处理采集结果
    }

    // 单条采集
    public Enterprise collect(String code) {
        return collector.collect(code);
    }

    // 批量采集
    public void batchCollect(List<String> codes) {
        CollectTask task = new CollectTask("enterprise", codes);
        taskProducer.sendTask(task);
    }

    // 采集结果处理
    @RabbitListener(queues = "#{taskResultQueue.name}")
    public void handleResult(TaskResult result) {
        // 处理采集结果
    }
}
