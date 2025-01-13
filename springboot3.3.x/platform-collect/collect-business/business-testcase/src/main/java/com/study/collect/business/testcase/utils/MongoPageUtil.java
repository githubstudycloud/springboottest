package com.study.collect.business.testcase.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.function.Consumer;

// MongoDB分页查询工具类
@Slf4j
public class MongoPageUtil {
    private static final int DEFAULT_BATCH_SIZE = 2000;

    /**
     * 分页查询MongoDB数据
     */
    public static <T> void pageQuery(MongoOperations mongoOperations,
                                     Query query,
                                     Class<T> entityClass,
                                     Consumer<List<T>> consumer) {
        pageQuery(mongoOperations, query, entityClass, DEFAULT_BATCH_SIZE, consumer);
    }

    /**
     * 分页查询MongoDB数据（指定批次大小）
     */
    public static <T> void pageQuery(MongoOperations mongoOperations,
                                     Query query,
                                     Class<T> entityClass,
                                     int batchSize,
                                     Consumer<List<T>> consumer) {
        long total = mongoOperations.count(query, entityClass);
        int pages = (int) Math.ceil((double) total / batchSize);

        for (int page = 0; page < pages; page++) {
            Query pageQuery = Query.from(query)
                    .skip((long) page * batchSize)
                    .limit(batchSize);

            List<T> batch = mongoOperations.find(pageQuery, entityClass);
            try {
                consumer.accept(batch);
            } catch (Exception e) {
                log.error("Error processing batch at page {}", page, e);
                throw new RuntimeException("Error processing batch", e);
            }
        }
    }
}
