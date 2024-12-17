package com.study.collect.infrastructure.persistent.mongo.repository;

import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.domain.repository.task.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 任务仓储实现
 */
@Repository
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public CollectTask save(CollectTask task) {
        return mongoTemplate.save(task);
    }

    @Override
    public List<CollectTask> saveAll(Iterable<CollectTask> tasks) {
        return StreamSupport.stream(tasks.spliterator(), false)
                .map(task -> this.save(task))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, CollectTask.class);
    }

    @Override
    public Optional<CollectTask> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, CollectTask.class));
    }

    @Override
    public List<CollectTask> findAll() {
        return mongoTemplate.findAll(CollectTask.class);
    }

    @Override
    public List<CollectTask> findByStatus(String status) {
        Query query = Query.query(Criteria.where("status").is(status));
        return mongoTemplate.find(query, CollectTask.class);
    }

    @Override
    public void updateStatus(String taskId, String status) {
        Query query = Query.query(Criteria.where("id").is(taskId));
        Update update = Update.update("status", status)
                .set("updateTime", LocalDateTime.now());
        mongoTemplate.updateFirst(query, update, CollectTask.class);
    }

    @Override
    public void incrementRetryTimes(String taskId) {
        Query query = Query.query(Criteria.where("id").is(taskId));
        Update update = new Update().inc("retryTimes", 1)
                .set("updateTime", LocalDateTime.now());
        mongoTemplate.updateFirst(query, update, CollectTask.class);
    }

    @Override
    public List<CollectTask> findTimeoutTasks(LocalDateTime timeout) {
        Query query = Query.query(Criteria.where("status").is("RUNNING")
                .and("startTime").lt(timeout));
        return mongoTemplate.find(query, CollectTask.class);
    }
}