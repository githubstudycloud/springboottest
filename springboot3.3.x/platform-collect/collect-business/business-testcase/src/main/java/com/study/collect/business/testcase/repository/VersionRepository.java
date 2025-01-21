package com.study.collect.business.testcase.repository;

import com.study.collect.business.testcase.entity.VersionEntity;
import com.study.collect.business.testcase.utils.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
public class VersionRepository {

    private static final String COLLECTION_NAME = "versions";
    private final MongoTemplate mongoTemplate;
    private final RateLimiter mongoRateLimiter;

    public VersionRepository(MongoTemplate mongoTemplate, RateLimiter mongoRateLimiter) {
        this.mongoTemplate = mongoTemplate;
        this.mongoRateLimiter = mongoRateLimiter;
    }

    /**
     * 保存版本信息
     */
    public VersionEntity save(VersionEntity entity) {
        try {
            mongoRateLimiter.acquire();
            return mongoTemplate.save(entity, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to save version: {}", entity, e);
            throw new RuntimeException("Failed to save version", e);
        }
    }

    /**
     * 批量保存版本信息
     */
    /**
     * 批量保存版本信息
     */
    public List<VersionEntity> saveAll(List<VersionEntity> entities) {
        try {
            mongoRateLimiter.acquire();
            // 使用 insertAll 改为 save，因为可能有更新的情况
            for (VersionEntity entity : entities) {
                mongoTemplate.save(entity, COLLECTION_NAME);
            }
            return entities;
        } catch (Exception e) {
            log.error("Failed to save versions, size: {}", entities.size(), e);
            throw new RuntimeException("Failed to save versions", e);
        }
    }

    /**
     * 根据根节点查询版本列表
     */
    public Page<String> findVersionsByRootNode(String rootNode, Pageable pageable) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(Criteria.where("root_node").is(rootNode))
                    .with(pageable);

            long total = mongoTemplate.count(query, VersionEntity.class, COLLECTION_NAME);
            List<VersionEntity> versions = mongoTemplate.find(query, VersionEntity.class, COLLECTION_NAME);

            return new PageImpl<>(
                    versions.stream().map(VersionEntity::getVersion).toList(),
                    pageable,
                    total
            );
        } catch (Exception e) {
            log.error("Failed to find versions for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to find versions", e);
        }
    }

    /**
     * 统计根节点的版本数量
     */
    public long countByRootNode(String rootNode) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(Criteria.where("root_node").is(rootNode));
            return mongoTemplate.count(query, VersionEntity.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to count versions for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to count versions", e);
        }
    }

    /**
     * 根据根节点和版本号查询
     */
    public VersionEntity findByRootNodeAndVersion(String rootNode, String version) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(
                    Criteria.where("root_node").is(rootNode)
                            .and("version").is(version)
            );
            return mongoTemplate.findOne(query, VersionEntity.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to find version: rootNode={}, version={}", rootNode, version, e);
            throw new RuntimeException("Failed to find version", e);
        }
    }

    /**
     * 查询最后更新时间
     */
    public LocalDateTime findLastUpdateTime(String rootNode) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(Criteria.where("root_node").is(rootNode))
                    .limit(1)
                    .with(org.springframework.data.domain.Sort.by(
                            org.springframework.data.domain.Sort.Direction.DESC, "update_time"));

            VersionEntity version = mongoTemplate.findOne(query, VersionEntity.class, COLLECTION_NAME);
            return version != null ? version.getUpdateTime() : null;
        } catch (Exception e) {
            log.error("Failed to find last update time for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to find last update time", e);
        }
    }
}