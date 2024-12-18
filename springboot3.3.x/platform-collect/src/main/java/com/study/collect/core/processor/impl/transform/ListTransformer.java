package com.study.collect.core.processor.impl.transform;

// 列表转换器
import com.study.collect.common.exception.collect.ProcessException;
import com.study.collect.core.processor.ProcessContext;
import com.study.collect.core.processor.base.AbstractProcessor;
import com.study.collect.common.enums.collect.ProcessType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 列表数据转换处理器
 */
@Slf4j
@Component
public class ListTransformer extends AbstractProcessor {

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
        if (!(rawData instanceof List)) {
            throw new ProcessException("Data is not a list");
        }

        try {
            List<Object> list = (List<Object>) rawData;

            // 转换列表数据
            List<Object> transformed = list.stream()
                    .map(this::transformItem)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            context.setResult(transformed);

        } catch (Exception e) {
            log.error("Transform list data failed", e);
            throw new ProcessException("Transform list data failed", e);
        }
    }

    /**
     * 转换单个数据项
     */
    protected Object transformItem(Object item) {
        // 默认实现,子类可覆盖
        return item;
    }
}