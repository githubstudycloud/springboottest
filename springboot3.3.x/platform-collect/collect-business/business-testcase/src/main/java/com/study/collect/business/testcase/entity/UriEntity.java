package com.study.collect.business.testcase.entity;

import com.study.collect.business.testcase.common.utils.HashUtil;
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

@Document(collection = "#{@collectionStrategy.getCollectionName('uri_collect')}")
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

    public UriEntity(String uri, String rootNode) {
        super(generateId(uri));
        this.uri = uri;
        this.uriHash = generateUriHash(uri);
        this.rootNode = rootNode;
    }

    private static String generateId(String uri) {
        return HashUtil.hash(uri);
    }

    private static String generateUriHash(String uri) {
        return HashUtil.hash(uri);
    }

    @PrePersist
    @Override
    public void prePersist() {
        super.prePersist();
        if (this.uriHash == null && this.uri != null) {
            this.uriHash = generateUriHash(this.uri);
        }
    }

    public void reset() {
        this.uri = null;
        this.uriHash = null;
        this.rootNode = null;
        this.versionType = null;
        this.uriVersion = null;
        this.details = null;
        this.deleted = false;
        this.version = 0L;
        this.versionCode = null;
        this.versionTime = null;
    }
}