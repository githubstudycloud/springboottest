package com.study.collect.core.processor.chain;

// 链构建器

import com.study.collect.core.processor.Processor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理器链构建器
 */
@Component
public class ProcessorChainBuilder {

    public ProcessorChain build(List<Processor> processors) {
        // 按顺序排序
        List<Processor> sortedProcessors = processors.stream()
                .sorted(Comparator.comparingInt(Processor::getOrder))
                .collect(Collectors.toList());
        return new ProcessorChain(sortedProcessors);
    }
}