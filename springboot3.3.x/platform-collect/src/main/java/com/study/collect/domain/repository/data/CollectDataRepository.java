package com.study.collect.domain.repository.data;

import com.study.collect.domain.entity.data.CollectData;
import com.study.collect.domain.repository.BaseRepository;

import java.util.List;

/**
 * 采集数据仓储接口
 */
public interface CollectDataRepository extends BaseRepository<CollectData, String> {
    /**
     * 根据任务ID查询数据
     */
    List<CollectData> findByTaskId(String taskId);

    /**
     * 根据类型查询数据
     */
    List<CollectData> findByType(String type);

    /**
     * 根据版本查询数据
     */
    List<CollectData> findByVersion(Long version);

    /**
     * 批量保存数据
     */
    void saveBatch(List<CollectData> dataList);
}