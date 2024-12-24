package com.study.collect.core.processor.chain;

import com.study.collect.core.processor.IProcessor;
import com.study.collect.core.processor.model.ProcessContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProcessChain<T> {

    private final List<IProcessor<T>> processors;
    private final String chainId;

    public ProcessChain(String chainId) {
        this.chainId = chainId;
        this.processors = new ArrayList<>();
    }

    public void addProcessor(IProcessor<T> processor) {
        processors.add(processor);
        processors.sort((p1, p2) -> p1.getOrder() - p2.getOrder());
    }

    public T process(T data) {
        ProcessContext context = new ProcessContext();
        context.setChainId(chainId);

        for (IProcessor<T> processor : processors) {
            try {
                data = processor.process(data, context);
                log.debug("处理器执行成功: processor={}, chainId={}",
                        processor.getType(), chainId);
            } catch (Exception e) {
                log.error("处理器执行失败: processor={}, chainId={}",
                        processor.getType(), chainId, e);
                throw e;
            }
        }

        return data;
    }
}