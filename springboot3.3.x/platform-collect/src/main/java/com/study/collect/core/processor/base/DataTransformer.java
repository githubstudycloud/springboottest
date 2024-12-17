package com.study.collect.core.processor.base;

import com.study.collect.common.enums.collect.ProcessType;

/**
 * 数据转换处理器
 */
@Component
@Slf4j
public class DataTransformer extends AbstractProcessor {

    @Override
    public ProcessType getType() {
        return ProcessType.TRANSFORM;
    }

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    protected void doProcess(ProcessContext context) {
        Object rawData = context.getRawData();
        if (rawData == null) {
            return;
        }

        try {
            // 执行数据转换
            Object result = transform(rawData);
            context.setResult(result);
        } catch (Exception e) {
            log.error("Transform data failed", e);
            throw new ProcessException("Transform data failed", e);
        }
    }

    protected Object transform(Object data) {
        // 具体转换逻辑由子类实现
        return data;
    }
}

