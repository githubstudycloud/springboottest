package com.study.collect.core.engine;

import com.study.collect.common.enums.collect.CollectType;

import java.time.LocalDateTime;

/**
 * 采集上下文
 */
@Data
@Builder
public class CollectContext {
    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 采集类型
     */
    private CollectType collectType;

    /**
     * 采集参数
     */
    private Map<String, Object> params;

    /**
     * 处理器链
     */
    private List<Processor> processors;

    /**
     * 采集开始时间
     */
    private LocalDateTime startTime;

    /**
     * 超时时间(ms)
     */
    private long timeout;
}
