package com.study.collect.core.processor;

import lombok.Builder;
import lombok.Data;

/**
 * 处理上下文
 */
@Data
@Builder
public class ProcessContext {
    /**
     * 原始数据
     */
    private Object rawData;

    /**
     * 处理结果
     */
    private Object result;

    /**
     * 处理参数
     */
    private Map<String, Object> params;

    /**
     * 是否继续处理
     */
    private boolean continueProcess = true;
}
