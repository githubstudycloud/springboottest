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
    private String version;

    @Field("root_node")
    private String rootNode;

    @Field("version_type")
    private String versionType;

    private String name;

    private String description;

    @Field("version_code")
    private String versionCode;

    @Field("version_time")
    private LocalDateTime versionTime;

    private Integer sort;

    public void initVersion() {
        this.version = String.valueOf(0L);
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    public void upgradeVersion() {
        this.version = this.version + 1;
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    private String generateVersionCode() {
        return String.format("%s%s%s%d",
                "V",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                "_",
                this.version);
    }
}