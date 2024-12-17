package com.study.collect.core.engine;

// 引擎接口
import com.study.collect.common.enums.collect.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * 采集引擎接口
 */
public interface CollectEngine {
    /**
     * 执行采集任务
     */
    CollectResult collect(CollectContext context);

    /**
     * 停止任务
     */
    void stop(String taskId);

    /**
     * 获取任务状态
     */
    TaskStatus getStatus(String taskId);
}
