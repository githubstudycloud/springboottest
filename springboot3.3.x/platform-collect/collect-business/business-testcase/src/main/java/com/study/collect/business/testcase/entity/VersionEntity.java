package com.study.collect.business.testcase.entity;

import com.study.collect.business.testcase.constant.CollectionConstants;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class VersionEntity extends BaseEntity {

    @Field("version_code")
    protected String versionCode;

    @Field("version_time")
    protected LocalDateTime versionTime;

    protected VersionEntity(String id) {
        super(id);
        initVersion();
    }

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
        return String.format("%s%s%s%d",
                CollectionConstants.VERSION_PREFIX,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                CollectionConstants.VERSION_SEPARATOR,
                this.version);
    }

    @PrePersist
    @Override
    public void prePersist() {
        super.prePersist();
        if (this.versionCode == null) {
            initVersion();
        }
    }

    @PreUpdate
    @Override
    public void preUpdate() {
        super.preUpdate();
        upgradeVersion();
    }
}