package com.study.collect.core.processor.chain;

// 处理器链

import com.study.collect.common.exception.collect.ProcessException;
import com.study.collect.core.processor.ProcessContext;
import lombok.RequiredArgsConstructor;

/**
 * 处理器链
 */
@RequiredArgsConstructor
public class ProcessorChain {

    private final List<Processor> processors;

    public void process(ProcessContext context) {
        for (Processor processor : processors) {
            if (!context.isContinueProcess()) {
                break;
            }
            try {
                processor.process(context);
            } catch (Exception e) {
                throw new ProcessException("Process failed at " + processor.getType(), e);
            }
        }
    }
}