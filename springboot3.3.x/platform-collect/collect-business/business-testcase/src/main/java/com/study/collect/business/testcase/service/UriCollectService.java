package com.study.collect.business.testcase.service;

import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.model.param.QueryParam;
import com.study.collect.business.testcase.model.response.AsyncResponse;
import com.study.collect.business.testcase.model.response.TaskResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * URI采集服务接口
 */
public interface UriCollectService {

    /**
     * 异步采集数据
     */
    AsyncResponse<String> collectData(CollectParam param);

    /**
     * 获取版本列表
     */
    Page<String> getVersions(String rootNode, Integer page, Integer size);

    /**
     * 获取版本下URI数量
     */
    Long getUriCount(String rootNode, String version);

    /**
     * 异步删除数据
     */
    AsyncResponse<Long> deleteData(DeleteParam param);

    /**
     * 条件查询URI数据
     */
    Page<UriEntity> queryUri(QueryParam param);

    /**
     * 批量查询URI
     */
    List<UriEntity> batchQueryUri(List<String> uris, Boolean includeDeleted, Boolean onlyDetail);

    /**
     * 按更新时间查询URI
     */
    Page<UriEntity> queryByUpdateTime(String rootNode, LocalDateTime startTime, LocalDateTime endTime,
                                      Integer page, Integer size);

    /**
     * 获取任务状态
     */
    AsyncResponse<Void> getTaskStatus(String taskId);

    /**
     * 取消任务
     */
    boolean cancelTask(String taskId);

    /**
     * 更新任务优先级
     */
    boolean updateTaskPriority(String taskId, int priority);

    /**
     * 获取活动任务列表
     */
    List<TaskResponse> getActiveTasks();

    /**
     * 获取采集统计信息
     */
    Map<String, Object> getCollectionStats(String rootNode);
}