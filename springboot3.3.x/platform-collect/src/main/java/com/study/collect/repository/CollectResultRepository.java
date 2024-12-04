package com.study.collect.repository;

import com.study.collect.entity.CollectResult;
import com.study.collect.enums.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectResultRepository extends MongoRepository<CollectResult, String> {
    List<CollectResult> findByTaskId(String taskId);

    List<CollectResult> findByTaskIdAndStatus(String taskId, TaskStatus status);

    void deleteByTaskId(String taskId);
}