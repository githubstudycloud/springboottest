package com.study.collect.business.testcase.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Document(collection = "versions")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "idx_root_version",
                def = "{'root_node': 1, 'version': 1}", unique = true)
})
public class VersionEntity extends BaseEntity {

    @Field("version")
    protected String version;

    @Field("root_node")
    protected String rootNode;

    @Field("version_type")
    protected String versionType;

    protected String name;

    protected String description;

    @Field("version_code")
    protected String versionCode;

    @Field("version_time")
    protected LocalDateTime versionTime;

    protected Integer sort;

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void reset() {
        super.reset();
        this.version = null;
        this.rootNode = null;
        this.versionType = null;
        this.name = null;
        this.description = null;
        this.versionCode = null;
        this.versionTime = null;
        this.sort = null;
    }

    public void initVersion() {
        this.version = "0";
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    public void upgradeVersion() {
        int currentVersion = Integer.parseInt(this.version);
        this.version = String.valueOf(currentVersion + 1);
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    private String generateVersionCode() {
        return String.format("V%s_%s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                this.version);
    }
}