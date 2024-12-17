package com.study.collect.core.processor;

import com.study.collect.common.enums.collect.ProcessType;

/**
 * 处理器接口
 */
public interface Processor {
    /**
     * 执行处理
     */
    void process(ProcessContext context);

    /**
     * 获取处理器类型
     */
    ProcessType getType();

    /**
     * 获取处理器顺序
     */
    int getOrder();
}
