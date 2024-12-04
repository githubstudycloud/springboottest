package com.study.collect.repository;

import com.study.collect.entity.CollectTask;
import com.study.collect.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<CollectTask, String> {
    List<CollectTask> findByStatus(TaskStatus status);

    List<CollectTask> findByStatusAndUpdateTimeBefore(TaskStatus status, Date updateTime);

    Page<CollectTask> findByStatusIn(List<TaskStatus> statuses, Pageable pageable);

    // 添加计数方法
    long countByStatus(TaskStatus status);

    @Query(value = "{ 'status' : ?0 }", count = true)
    long getTaskCountByStatus(TaskStatus status);

    // 添加时间范围查询方法
    @Query("{'status' : ?0, 'createTime' : { $gte: ?1, $lte: ?2 }}")
    List<CollectTask> findByStatusAndTimeRange(TaskStatus status, Date startTime, Date endTime);
}