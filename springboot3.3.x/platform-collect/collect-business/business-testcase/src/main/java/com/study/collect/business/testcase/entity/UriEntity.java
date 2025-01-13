package com.study.collect.business.testcase.entity;

import com.study.collect.core.storage.entity.VersionEntity;
import com.study.collect.business.testcase.utils.HashUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "#{@collectionStrategy.getCollectionName('uri_collect')}") // 从配置文件中获取集合名称
@Data
@EqualsAndHashCode(callSuper = true)
@CompoundIndexes({
        @CompoundIndex(name = "uri_unique_idx",
                def = "{'uri': 1, 'rootNode': 1, 'versionType': 1, 'uriVersion': 1}",
                unique = true),
        @CompoundIndex(name = "uriHash_idx",
                def = "{'uriHash': 1}",
                unique = true)
})
@NoArgsConstructor  // 添加无参构造器
@AllArgsConstructor
public class UriEntity extends VersionEntity {
    @Indexed(unique = true)
    private String uriHash;

    @Indexed
    private String uri;
    private String rootNode;
    private String versionType;
    private String uriVersion;
    private Map<String, Object> details;

//    @Override
    public void prePersist() {
        if (this.uriHash == null && this.uri != null) {
            this.uriHash = HashUtil.hash(this.uri);
        }
        this.version = 0L;
    }
}

