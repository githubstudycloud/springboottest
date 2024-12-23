package com.study.collect.infrastructure.storage.template.impl;

import com.study.collect.infrastructure.storage.entity.VersionEntity;
import com.study.collect.infrastructure.storage.template.VersionTemplate;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class VersionTemplateImpl extends MongoOperationTemplateImpl<VersionEntity> implements VersionTemplate {

    public VersionTemplateImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, VersionEntity.class);
    }

    @Override
    public Optional<VersionEntity> findLatestVersion(String businessId) {
        Query query = new Query(Criteria.where("businessId").is(businessId))
                .with(Sort.by(Sort.Direction.DESC, "versionNumber"))
                .limit(1);
        return findOne(query);
    }

    @Override
    public List<VersionEntity> findVersionHistory(String businessId) {
        Query query = new Query(Criteria.where("businessId").is(businessId))
                .with(Sort.by(Sort.Direction.DESC, "versionNumber"));
        return findList(query);
    }

    @Override
    public void createNewVersion(VersionEntity version) {
        // 设置版本号
        String nextVersion = generateNextVersion(version.getBusinessId());
        version.setVersionNumber(nextVersion);
        insert(version);
    }

    @Override
    public void rollbackToVersion(String businessId, String versionNumber) {
        // 实现版本回滚逻辑
        Query query = new Query(Criteria.where("businessId").is(businessId)
                .and("versionNumber").is(versionNumber));
        Optional<VersionEntity> targetVersion = findOne(query);
        
        targetVersion.ifPresent(version -> {
            VersionEntity newVersion = version.clone();  // 假设实现了clone方法
            createNewVersion(newVersion);
        });
    }

    @Override
    public void compareVersions(String businessId, String version1, String version2) {
        // 实现版本比较逻辑
        Query query = new Query(Criteria.where("businessId").is(businessId)
                .and("versionNumber").in(version1, version2));
        List<VersionEntity> versions = findList(query);
        // 实现具体的比较逻辑
    }

    @Override
    public void cleanHistoryVersions(String businessId, int reserveCount) {
        // 保留最新的N个版本，删除其他版本
        Query query = new Query(Criteria.where("businessId").is(businessId))
                .with(Sort.by(Sort.Direction.DESC, "versionNumber"))
                .skip(reserveCount);
        remove(query);
    }

    @Override
    public void lockVersion(String businessId, String version) {
        Query query = new Query(Criteria.where("businessId").is(businessId)
                .and("versionNumber").is(version));
        Update update = new Update().set("locked", true);
        update(query, update);
    }

    @Override
    public void unlockVersion(String businessId, String version) {
        Query query = new Query(Criteria.where("businessId").is(businessId)
                .and("versionNumber").is(version));
        Update update = new Update().set("locked", false);
        update(query, update);
    }

    private String generateNextVersion(String businessId) {
        // 生成下一个版本号的逻辑
        Optional<VersionEntity> latestVersion = findLatestVersion(businessId);
        if (latestVersion.isEmpty()) {
            return "v1.0.0";
        }
        String currentVersion = latestVersion.get().getVersionNumber();
        // 实现版本号增长逻辑
        return incrementVersion(currentVersion);
    }

    private String incrementVersion(String version) {
        // 版本号增长逻辑实现
        String[] parts = version.substring(1).split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);
        
        patch++;
        if (patch > 99) {
            patch = 0;
            minor++;
            if (minor > 99) {
                minor = 0;
                major++;
            }
        }
        
        return String.format("v%d.%d.%d", major, minor, patch);
    }
}
