package com.study.collect.business.testcase.core.processor;

import com.study.collect.business.testcase.common.utils.StreamProcessor.ProcessMetrics;
import java.util.concurrent.CompletableFuture;

/**
 * 数据处理器接口
 * @param <T> 处理参数类型
 * @param <R> 结果类型
 */
public interface DataProcessor<T, R> {

    /**
     * 异步处理数据
     * @param param 处理参数
     * @return 异步处理结果
     */
    CompletableFuture<R> process(T param);

    /**
     * 取消处理
     * @param taskId 任务ID
     * @return 是否成功取消
     */
    boolean cancel(String taskId);

    /**
     * 更新优先级
     * @param taskId 任务ID
     * @param priority 新优先级
     * @return 是否成功更新
     */
    boolean updatePriority(String taskId, int priority);

    /**
     * 获取处理进度
     * @param taskId 任务ID
     * @return 处理进度指标
     */
    ProcessMetrics getProgress(String taskId);

    /**
     * 暂停处理
     * @param taskId 任务ID
     */
    void pause(String taskId);

    /**
     * 恢复处理
     * @param taskId 任务ID
     */
    void resume(String taskId);
}