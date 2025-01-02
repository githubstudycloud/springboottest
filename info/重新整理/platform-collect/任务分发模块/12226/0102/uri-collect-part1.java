// api/controller/UriCollectController.java
package com.study.collect.api.controller;

import com.study.collect.domain.param.CollectParam;
import com.study.collect.service.UriCollectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/collect")
@RequiredArgsConstructor
@Slf4j
public class UriCollectController {
    private final UriCollectService collectService;
    
    @PostMapping("/sync")
    public ResponseEntity<Void> syncData(@RequestBody CollectParam param) {
        collectService.collectData(param);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/uri")
    public ResponseEntity<List<UriEntity>> queryUri(
            @RequestParam(required = false) String rootNode,
            @RequestParam(required = false) String version,
            @RequestParam(required = false) String versionType) {
        return ResponseEntity.ok(collectService.queryUri(rootNode, version, versionType));
    }
}

// api/response/BaseResponse.java
package com.study.collect.api.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class BaseResponse {
    private String code;
    private String message;
}

// api/response/PageResponse.java
package com.study.collect.api.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PageResponse<T> extends BaseResponse {
    private Long total;
    private List<T> items;
}

// api/response/VersionResponse.java
package com.study.collect.api.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class VersionResponse extends BaseResponse {
    private String version;
    private String versionType;
    private String description;
}