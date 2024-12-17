package com.study.collect.core.engine;

// 抽象基类

import com.study.collect.common.enums.collect.CollectType;
import com.study.collect.common.enums.collect.TaskStatus;
import com.study.collect.common.utils.data.ValidateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;

/**
 * 采集引擎抽象基类
 */
@Slf4j
public abstract class AbstractCollectEngine implements CollectEngine {

    @Override
    public CollectResult collect(CollectContext context) {
        try {
            // 前置处理
            preCollect(context);

            // 执行采集
            CollectResult result = doCollect(context);

            // 后置处理
            postCollect(context, result);

            return result;
        } catch (Exception e) {
            log.error("Collect failed", e);
            return CollectResult.error(e.getMessage());
        }
    }

    /**
     * 前置处理
     */
    protected void preCollect(CollectContext context) {
        // 参数校验
        validateContext(context);
        // 初始化上下文
        initContext(context);
        // 记录开始时间
        context.setStartTime(LocalDateTime.now());
    }

    /**
     * 执行采集
     */
    protected abstract CollectResult doCollect(CollectContext context);

    /**
     * 后置处理
     */
    protected void postCollect(CollectContext context, CollectResult result) {
        // 更新任务状态
        updateTaskStatus(context.getTaskId(), result.isSuccess() ?
                TaskStatus.SUCCESS : TaskStatus.FAILED);
        // 记录执行结果
        saveResult(context, result);
    }

    /**
     * 参数校验
     */
    protected void validateContext(CollectContext context) {
        ValidateUtils.notNull(context, "Context cannot be null");
        ValidateUtils.notEmpty(context.getTaskId(), "TaskId cannot be empty");
        ValidateUtils.notNull(context.getCollectType(), "CollectType cannot be null");
    }

    /**
     * 初始化上下文
     */
    protected void initContext(CollectContext context) {
        if (context.getTimeout() <= 0) {
            context.setTimeout(DEFAULT_TIMEOUT);
        }
        if (CollectionUtils.isEmpty(context.getProcessors())) {
            context.setProcessors(getDefaultProcessors(context.getCollectType()));
        }
    }

    /**
     * 获取默认处理器链
     */
    protected abstract List<Processor> getDefaultProcessors(CollectType type);
}