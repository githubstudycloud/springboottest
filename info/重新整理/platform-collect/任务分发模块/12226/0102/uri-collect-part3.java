// domain/entity/UriEntity.java
package com.study.collect.domain.entity;

import com.study.collect.core.util.HashUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

@Document(collection = "uri_collect")
@Data
@EqualsAndHashCode(callSuper = true)
public class UriEntity extends VersionEntity {
    @Indexed
    private String uriHash;
    private String uri;
    private String rootNode;
    private String versionType;
    private String uriVersion;
    private Map<String, Object> details;
    
    @Override
    public void prePersist() {
        if (this.uriHash == null) {
            this.uriHash = HashUtil.hash(this.uri);
        }
        this.version = 0L;
    }
}

// domain/param/CollectParam.java
package com.study.collect.domain.param;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CollectParam {
    private String rootNode;
    private String version;
    private Boolean incremental = false;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

// domain/param/PageParam.java
package com.study.collect.domain.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParam {
    private int page = 1;
    private int size = 200;
}