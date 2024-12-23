package com.study.collect.infrastructure.storage.template;

import com.study.collect.infrastructure.storage.entity.VersionEntity;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

/**
 * 版本数据操作模板
 */
public interface VersionTemplate extends MongoOperationTemplate<VersionEntity> {
    
    // 版本控制相关操作
    Optional<VersionEntity> findLatestVersion(String businessId);
    
    List<VersionEntity> findVersionHistory(String businessId);
    
    void createNewVersion(VersionEntity version);
    
    void rollbackToVersion(String businessId, String versionNumber);
    
    // 差异比较
    void compareVersions(String businessId, String version1, String version2);
    
    // 清理历史版本
    void cleanHistoryVersions(String businessId, int reserveCount);
    
    // 版本锁定/解锁
    void lockVersion(String businessId, String version);
    
    void unlockVersion(String businessId, String version);
}
