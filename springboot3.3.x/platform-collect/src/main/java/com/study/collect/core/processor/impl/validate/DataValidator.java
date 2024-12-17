package com.study.collect.core.processor.impl.validate;

// 数据校验器

import com.study.collect.common.enums.collect.ProcessType;
import com.study.collect.core.processor.ProcessContext;

/**
 * 数据验证处理器
 */
@Component
@Slf4j
public class DataValidator extends AbstractProcessor {

    @Override
    public ProcessType getType() {
        return ProcessType.VALIDATE;
    }

    @Override
    public int getOrder() {
        return 200;
    }

    @Override
    protected void doProcess(ProcessContext context) {
        Object data = context.getResult();
        if (data == null) {
            throw new ValidateException("Data cannot be null");
        }

        try {
            // 执行数据验证
            validate(data);
        } catch (Exception e) {
            log.error("Validate data failed", e);
            throw new ValidateException("Validate data failed: " + e.getMessage());
        }
    }

    protected void validate(Object data) {
        // 具体验证逻辑由子类实现
    }
}
