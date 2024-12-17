package com.study.collect.infrastructure.persistent.cache.repository;

import com.study.collect.domain.entity.task.CollectTask;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * 任务缓存仓储
 */
@Repository
@RequiredArgsConstructor
public class TaskCacheRepository extends BaseCacheRepository {

    private static final String TASK_KEY_PREFIX = "task:";
    private static final String TASK_COUNT_KEY = "task:count";
    private static final long DEFAULT_EXPIRE_TIME = 24;

    public void saveTask(String taskId, CollectTask task) {
        String key = TASK_KEY_PREFIX + taskId;
        set(key, task, DEFAULT_EXPIRE_TIME, TimeUnit.HOURS);
        increment(TASK_COUNT_KEY);
    }

    public CollectTask getTask(String taskId) {
        String key = TASK_KEY_PREFIX + taskId;
        return get(key, CollectTask.class);
    }

    public void deleteTask(String taskId) {
        String key = TASK_KEY_PREFIX + taskId;
        delete(key);
        increment(TASK_COUNT_KEY, -1);
    }

    public Long getTaskCount() {
        Object value = get(TASK_COUNT_KEY, Long.class);
        return value == null ? 0L : (Long) value;
    }
}
