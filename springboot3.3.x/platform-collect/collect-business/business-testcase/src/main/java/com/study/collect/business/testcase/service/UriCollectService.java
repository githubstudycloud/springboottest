package com.study.collect.business.testcase.service;

import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.model.param.QueryParam;
import com.study.collect.business.testcase.model.response.AsyncResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * URI采集服务接口
 */
public interface UriCollectService {

    /**
     * 异步采集数据
     * @param param 采集参数
     * @return 异步响应，包含任务ID
     */
    AsyncResponse<String> collectData(CollectParam param);

    /**
     * 异步删除数据
     * @param param 删除参数
     * @return 异步响应，包含删除结果
     */
    AsyncResponse<Long> deleteData(DeleteParam param);

    /**
     * 查询URI数据
     * @param param 查询参数
     * @return 分页结果
     */
    Page<UriEntity> queryUri(QueryParam param);

    /**
     * 批量查询URI数据
     * @param uris URI列表
     * @param includeDeleted 是否包含已删除数据
     * @return URI实体列表
     */
    List<UriEntity> batchQueryUri(List<String> uris, Boolean includeDeleted);

    /**
     * 获取任务状态
     * @param taskId 任务ID
     * @return 任务状态
     */
    AsyncResponse<Void> getTaskStatus(String taskId);

    /**
     * 取消任务
     * @param taskId 任务ID
     * @return 是否成功取消
     */
    boolean cancelTask(String taskId);

    /**
     * 更新任务优先级
     * @param taskId 任务ID
     * @param priority 新优先级
     * @return 是否成功更新
     */
    boolean updateTaskPriority(String taskId, int priority);

    /**
     * 获取活动任务列表
     * @return 活动任务列表
     */
    List<AsyncResponse<Void>> getActiveTasks();
}