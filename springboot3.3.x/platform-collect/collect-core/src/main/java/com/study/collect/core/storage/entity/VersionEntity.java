package com.study.collect.core.storage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// VersionEntity.java
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor  // 添加无参构造器
public abstract class VersionEntity extends BaseEntity {

    protected String versionCode;    // 业务版本号
    protected LocalDateTime versionTime;  // 版本时间

    public void initVersion() {
        this.version = 0L;
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    public void upgradeVersion() {
        this.version = this.version + 1;
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    protected String generateVersionCode() {
        return String.format("V%s_%d",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                this.version);
    }
}