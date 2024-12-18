package com.study.collect.core.processor.chain;

// 链构建器
import com.study.collect.core.processor.Processor;
import com.study.collect.core.processor.impl.validate.DataValidator;
import com.study.collect.core.processor.impl.transform.DataTransformer;
import com.study.collect.core.processor.impl.storage.CacheProcessor;
import com.study.collect.core.processor.impl.storage.MongoProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理器链构建器
 */
@Component
public class ProcessorChainBuilder {

    /**
     * 构建处理器链
     */
    public ProcessorChain build(List<Processor> processors) {
        // 1. 验证和过滤
        List<Processor> validProcessors = validateProcessors(processors);

        // 2. 排序处理器
        List<Processor> sortedProcessors = sortProcessors(validProcessors);

        // 3. 添加默认处理器
        List<Processor> finalProcessors = addDefaultProcessors(sortedProcessors);

        // 4. 创建处理器链
        return new ProcessorChain(finalProcessors);
    }

    /**
     * 验证处理器
     */
    private List<Processor> validateProcessors(List<Processor> processors) {
        if (processors == null) {
            return new ArrayList<>();
        }
        return processors.stream()
                .filter(p -> p != null && p.getType() != null)
                .collect(Collectors.toList());
    }

    /**
     * 排序处理器
     */
    private List<Processor> sortProcessors(List<Processor> processors) {
        return processors.stream()
                .sorted(Comparator.comparingInt(Processor::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * 添加默认处理器
     */
    private List<Processor> addDefaultProcessors(List<Processor> processors) {
        List<Processor> result = new ArrayList<>(processors);

        // 添加基础处理器
        result.add(new DataTransformer()); // 数据转换
        result.add(new DataValidator());   // 数据验证
        result.add(new CacheProcessor());  // 缓存处理
        result.add(new MongoProcessor());  // 存储处理

        return result;
    }

    /**
     * 构建自定义处理器链
     */
    public ProcessorChain buildCustom(List<Processor> processors, boolean addDefault) {
        List<Processor> validProcessors = validateProcessors(processors);
        List<Processor> sortedProcessors = sortProcessors(validProcessors);

        if (addDefault) {
            sortedProcessors = addDefaultProcessors(sortedProcessors);
        }

        return new ProcessorChain(sortedProcessors);
    }
}

///**
// * 数据处理链构建器
// */
//@Slf4j
//@Component
//public class ProcessorChainBuilder {
//
//    /**
//     * 构建处理器链
//     */
//    public ProcessorChain build(List<Processor> processors) {
//        // 1. 验证处理器
//        validateProcessors(processors);
//
//        // 2. 排序处理器
//        List<Processor> sortedProcessors = sortProcessors(processors);
//
//        // 3. 添加监控处理器
//        sortedProcessors.add(0, new MonitorProcessor());
//        sortedProcessors.add(new LogProcessor());
//
//        // 4. 构建处理器链
//        return new ProcessorChain(sortedProcessors);
//    }
//
//    private void validateProcessors(List<Processor> processors) {
//        // 验证处理器不为空
//        if (CollectionUtils.isEmpty(processors)) {
//            throw new IllegalArgumentException("Processors cannot be empty");
//        }
//
//        // 验证处理器类型
//        processors.forEach(processor -> {
//            if (processor.getType() == null) {
//                throw new IllegalArgumentException(
//                        "Processor type cannot be null: " + processor.getClass()
//                );
//            }
//        });
//    }
//
//    private List<Processor> sortProcessors(List<Processor> processors) {
//        return processors.stream()
//                .sorted(Comparator.comparingInt(Processor::getOrder))
//                .collect(Collectors.toList());
//    }
//}
