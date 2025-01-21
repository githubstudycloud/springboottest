package com.study.collect.business.testcase.repository;

import com.study.collect.business.testcase.entity.CollectTaskEntity;
import com.study.collect.business.testcase.utils.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
public class CollectTaskRepository {

    private final MongoTemplate mongoTemplate;
    private final RateLimiter mongoRateLimiter;
    private static final String COLLECTION_NAME = "collect_tasks";

    public CollectTaskRepository(MongoTemplate mongoTemplate, RateLimiter mongoRateLimiter) {
        this.mongoTemplate = mongoTemplate;
        this.mongoRateLimiter = mongoRateLimiter;
    }

    /**
     * 保存任务
     */
    public CollectTaskEntity save(CollectTaskEntity task) {
        try {
            mongoRateLimiter.acquire();
            return mongoTemplate.save(task, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to save task: {}", task.getTaskId(), e);
            throw new RuntimeException("Failed to save task", e);
        }
    }

    /**
     * 根据任务ID查询
     */
    public CollectTaskEntity findByTaskId(String taskId) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(Criteria.where("task_id").is(taskId));
            return mongoTemplate.findOne(query, CollectTaskEntity.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to find task: {}", taskId, e);
            throw new RuntimeException("Failed to find task", e);
        }
    }

    /**
     * 查询活跃任务
     */
    public List<CollectTaskEntity> findActiveTasks() {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(
                    Criteria.where("status").in("CREATED", "PROCESSING")
            );
            return mongoTemplate.find(query, CollectTaskEntity.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to find active tasks", e);
            throw new RuntimeException("Failed to find active tasks", e);
        }
    }

    /**
     * 查询根节点的最后采集时间
     */
    public LocalDateTime findLastCollectTime(String rootNode) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(
                            Criteria.where("root_node").is(rootNode)
                                    .and("status").is("COMPLETED")
                    )
                    .with(Sort.by(Sort.Direction.DESC, "end_time"))
                    .limit(1);

            CollectTaskEntity task = mongoTemplate.findOne(query, CollectTaskEntity.class, COLLECTION_NAME);
            return task != null ? task.getEndTime() : null;
        } catch (Exception e) {
            log.error("Failed to find last collect time for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to find last collect time", e);
        }
    }

    /**
     * 查询失败的任务
     */
    public List<CollectTaskEntity> findFailedTasks(LocalDateTime before) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(
                    Criteria.where("status").is("FAILED")
                            .and("end_time").lt(before)
                            .and("retry_count").lt(3)
            );
            return mongoTemplate.find(query, CollectTaskEntity.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to find failed tasks before: {}", before, e);
            throw new RuntimeException("Failed to find failed tasks", e);
        }
    }

    /**
     * 查询超时任务
     */
    public List<CollectTaskEntity> findTimeoutTasks(LocalDateTime timeoutThreshold) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(
                    Criteria.where("status").is("PROCESSING")
                            .and("start_time").lt(timeoutThreshold)
            );
            return mongoTemplate.find(query, CollectTaskEntity.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to find timeout tasks before: {}", timeoutThreshold, e);
            throw new RuntimeException("Failed to find timeout tasks", e);
        }
    }

    /**
     * 清理历史任务
     */
    public void deleteHistoryTasks(LocalDateTime before) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(
                    Criteria.where("end_time").lt(before)
                            .and("status").in("COMPLETED", "FAILED", "CANCELLED")
            );
            mongoTemplate.remove(query, CollectTaskEntity.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to delete history tasks before: {}", before, e);
            throw new RuntimeException("Failed to delete history tasks", e);
        }
    }
}