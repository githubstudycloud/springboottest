package com.study.collect.core.storage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Version;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class VersionEntity extends BaseEntity {

    @Version
    private Long version;

    private String versionCode; // 业务版本号,用于增量同步

    private LocalDateTime versionTime; // 版本时间戳

    // 版本初始化
    public void initVersion() {
        this.version = 0L;
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    // 版本更新
    public void upgradeVersion() {
        this.version = this.version + 1;
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    // 生成版本号
    private String generateVersionCode() {
        return String.format("V%s_%d",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                this.version);
    }
}