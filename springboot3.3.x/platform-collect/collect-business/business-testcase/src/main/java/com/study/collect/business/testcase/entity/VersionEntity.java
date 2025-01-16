package com.study.collect.business.testcase.entity;

import com.study.collect.business.testcase.common.utils.TableNameHelper;
import com.study.collect.core.storage.entity.VersionEntity;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;
import java.util.Objects;

/**
 * URI实体类
 */
@Document(collection = "#{@tableNameHelper.getTableName(#root.rootNode)}")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@CompoundIndexes({
        @CompoundIndex(
                name = "uri_unique_idx",
                def = "{'uri': 1, 'root_node': 1, 'version_type': 1, 'uri_version': 1}",
                unique = true,
                background = true
        ),
        @CompoundIndex(
                name = "uri_hash_idx",
                def = "{'uri_hash': 1}",
                unique = true,
                background = true
        ),
        @CompoundIndex(
                name = "query_idx",
                def = "{'root_node': 1, 'version_type': 1, 'uri_version': 1, 'is_deleted': 1}",
                background = true
        ),
        @CompoundIndex(
                name = "version_idx",
                def = "{'version_code': 1, 'version_time': 1}",
                background = true
        )
})
public class UriEntity extends VersionEntity {

    @Indexed(unique = true, background = true)
    @Field("uri_hash")
    private String uriHash;

    @Indexed(background = true)
    private String uri;

    @Field("root_node")
    private String rootNode;

    @Field("version_type")
    private String versionType;

    @Field("uri_version")
    private String uriVersion;

    private Map<String, Object> details;

    /**
     * 构造函数
     */
    public UriEntity(String uri, String rootNode) {
        super(generateId(uri));
        this.uri = uri;
        this.uriHash = generateUriHash(uri);
        this.rootNode = rootNode;
        this.details = null;
    }

    private static String generateId(String uri) {
        return TableNameHelper.generateDocumentId(uri, null);
    }

    private static String generateUriHash(String uri) {
        return TableNameHelper.generateUriHash(uri);
    }

    @PrePersist
    @Override
    public void prePersist() {
        super.prePersist();
        if (this.uriHash == null && this.uri != null) {
            this.uriHash = generateUriHash(this.uri);
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.uri = null;
        this.uriHash = null;
        this.rootNode = null;
        this.versionType = null;
        this.uriVersion = null;
        this.details = null;
    }

    /**
     * 拷贝实体内容（不含ID和版本信息）
     */
    public void copyFrom(UriEntity other) {
        this.uri = other.getUri();
        this.uriHash = other.getUriHash();
        this.rootNode = other.getRootNode();
        this.versionType = other.getVersionType();
        this.uriVersion = other.getUriVersion();
        this.details = other.getDetails();
    }

    /**
     * 检查是否需要更新
     */
    public boolean needsUpdate(UriEntity other) {
        if (other == null) {
            return false;
        }

        // 检查URI和Hash是否改变
        if (!Objects.equals(this.uri, other.getUri()) ||
                !Objects.equals(this.uriHash, other.getUriHash())) {
            return true;
        }

        // 检查版本信息是否改变
        if (!Objects.equals(this.versionType, other.getVersionType()) ||
                !Objects.equals(this.uriVersion, other.getUriVersion())) {
            return true;
        }

        // 检查详情是否改变
        return !Objects.equals(this.details, other.getDetails());
    }
}