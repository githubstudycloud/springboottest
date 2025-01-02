// repository/UriRepository.java
package com.study.collect.repository;

import com.study.collect.core.storage.repository.BaseMongoRepository;
import com.study.collect.domain.entity.UriEntity;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

@Repository
public interface UriRepository extends BaseMongoRepository<UriEntity> {
    
    @Override
    default <S extends UriEntity> S save(S entity) {
        Query query = new Query(Criteria.where("uriHash").is(entity.getUriHash()));
        Update update = new Update()
                .set("uri", entity.getUri())
                .set("rootNode", entity.getRootNode())
                .set("versionType", entity.getVersionType())
                .set("uriVersion", entity.getUriVersion())
                .set("details", entity.getDetails())
                .set("updateTime", LocalDateTime.now());
        
        mongoOperations.upsert(query, update, UriEntity.class);
        return entity;
    }

    @Override
    default <S extends UriEntity> List<S> saveAll(Iterable<S> entities) {
        BulkOperations bulkOps = mongoOperations.bulkOps(BulkOperations.BulkMode.UNORDERED, UriEntity.class);
        
        StreamSupport.stream(entities.spliterator(), false)
                .forEach(entity -> {
                    Query query = Query.query(Criteria.where("uriHash").is(entity.getUriHash()));
                    Update update = new Update()
                            .set("uri", entity.getUri())
                            .set("rootNode", entity.getRootNode())
                            .set("versionType", entity.getVersionType())
                            .set("uriVersion", entity.getUriVersion())
                            .set("details", entity.getDetails())
                            .set("updateTime", LocalDateTime.now());
                    
                    bulkOps.upsert(query, update);
                });

        bulkOps.execute();
        return (List<S>) entities;
    }

    default List<UriEntity> findByConditions(String rootNode, String version, String versionType) {
        Criteria criteria = new Criteria();
        
        if (rootNode != null) {
            criteria.and("rootNode").is(rootNode);
        }
        if (version != null) {
            criteria.and("uriVersion").is(version);
        }
        if (versionType != null) {
            criteria.and("versionType").is(versionType);
        }
        
        return mongoOperations.find(Query.query(criteria), UriEntity.class);
    }

    void deleteByUriHashNotIn(Collection<String> uriHashes);
}