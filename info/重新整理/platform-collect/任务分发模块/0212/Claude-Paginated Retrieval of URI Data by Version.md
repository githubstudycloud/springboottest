## Prompt:
我需要查询单个小版本的的所有数据并可控分页返回应该怎么做，查List《uti》自动转list hash然后分页查询返回应该怎么做，请参考代码给出这个这个逻辑应该怎么实现，对于采集入库的数据

## Response:
我来帮你实现这个功能。我们需要在UriCollectService接口中添加一个新方法，并在实现类中实现这个功能。主要思路是:

1.  首先根据rootNode和version查询得到所有URI的hash值
2.  对hash值列表进行分页
3.  根据分页后的hash值查询详细数据
4.  返回分页结果

以下是具体实现代码：

1.  首先在UriCollectService接口中添加新方法：

```java
public interface UriCollectService {
    // 其他已有方法...
    
    /**
     * 分页查询指定版本的URI数据
     * @param rootNode 根节点
     * @param version 版本号
     * @param page 页码(从1开始)
     * @param size 每页大小
     * @return 分页结果
     */
    Page<UriEntity> queryUrisByVersion(String rootNode, String version, int page, int size);
}
```

2.  在UriCollectServiceImpl中实现该方法：

```java
@Override
public Page<UriEntity> queryUrisByVersion(String rootNode, String version, int page, int size) {
    if (!StringUtils.hasText(rootNode) || !StringUtils.hasText(version)) {
        throw new IllegalArgumentException("RootNode and version cannot be empty");
    }
    
    try {
        // 1. 获取总记录数
        long total = uriRepository.countByRootNodeAndVersion(rootNode, version);
        
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page - 1, size), 0);
        }
        
        // 2. 分页获取uriHash列表
        List<String> uriHashes = uriRepository.findUriHashesNativeWithPage(
            rootNode,
            version,
            null, // versionType可以为null
            page,
            size
        );
        
        if (CollectionUtils.isEmpty(uriHashes)) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page - 1, size), total);
        }
        
        // 3. 根据hash列表查询完整数据
        Query query = new Query(Criteria.where("uri_hash").in(uriHashes));
        List<UriEntity> entities = mongoTemplate.find(query, UriEntity.class, 
            String.format("%s_%s", CollectionConstants.URI_COLLECTION_PREFIX, rootNode));
        
        // 4. 按照原hash列表的顺序排序结果
        Map<String, UriEntity> entityMap = entities.stream()
            .collect(Collectors.toMap(UriEntity::getUriHash, Function.identity()));
        
        List<UriEntity> orderedEntities = uriHashes.stream()
            .map(entityMap::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        return new PageImpl<>(orderedEntities, PageRequest.of(page - 1, size), total);
        
    } catch (Exception e) {
        log.error("Failed to query URIs by version: rootNode={}, version={}", rootNode, version, e);
        throw new RuntimeException("Failed to query URIs by version", e);
    }
}
```

3.  在Controller层添加对应的接口：

```java
@GetMapping("/version/{rootNode}/{version}")
@ApiOperation("Get paginated URIs by version")
public ResponseEntity<Page<UriEntity>> getUrisByVersion(
        @PathVariable @NotNull String rootNode,
        @PathVariable @NotNull String version,
        @RequestParam(defaultValue = "1") @Min(1) Integer page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(1000) Integer size) {
    return ResponseEntity.ok(collectService.queryUrisByVersion(rootNode, version, page, size));
}
```

这个实现的主要特点：

1.  使用了已有的findUriHashesNativeWithPage方法来获取分页的hash列表，避免了内存中分页
2.  通过hash列表批量查询实体数据，减少数据库查询次数
3.  保持了结果的顺序性，与hash列表顺序一致
4.  使用了已有的MongoDB索引来优化查询性能
5.  适当的错误处理和日志记录

使用这个API的方式：

```bash
GET /api/collect/version/{rootNode}/{version}?page=1&size=20
```

这个实现充分利用了现有的MongoDB索引，特别是uri\_hash和复合索引，可以保证查询性能。同时通过分页机制控制了内存使用，可以处理大量数据的情况。

如果你需要特定的字段过滤或者其他查询条件，我们可以进一步扩展这个方法。

## Prompt:
给出一下完整的代码并优化

## Response:
我将设计一个完整且优化的实现，包括必要的实体对象、参数校验和错误处理。

首先，我们创建一个查询参数对象：

```java
package com.study.collect.business.testcase.model.param;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.*;

@Data
@Builder
public class VersionQueryParam {
    @NotBlank(message = "rootNode cannot be empty")
    private String rootNode;
    
    @NotBlank(message = "version cannot be empty")
    private String version;
    
    @Min(value = 1, message = "page must be greater than 0")
    @Builder.Default
    private int page = 1;
    
    @Min(value = 1, message = "size must be greater than 0")
    @Max(value = 1000, message = "size must not exceed 1000")
    @Builder.Default
    private int size = 20;
    
    private Boolean includeDeleted;
    private Boolean onlyDetail;
    private String sortField;
    private String sortOrder;
}
```

然后更新Service接口：

```java
package com.study.collect.business.testcase.service;

public interface UriCollectService {
    // ... 其他现有方法 ...
    
    /**
     * 分页查询指定版本的URI数据
     * @param param 查询参数
     * @return 分页结果
     */
    Page<UriEntity> queryUrisByVersion(VersionQueryParam param);
}
```

下面是优化后的实现类：

```java
package com.study.collect.business.testcase.service.impl;

@Slf4j
@Service
@RequiredArgsConstructor
public class UriCollectServiceImpl implements UriCollectService {
    private final MongoTemplate mongoTemplate;
    private final UriRepository uriRepository;
    private final RateLimiter mongoRateLimiter;
    
    @Override
    public Page<UriEntity> queryUrisByVersion(VersionQueryParam param) {
        long startTime = System.currentTimeMillis();
        log.debug("Starting version query with params: {}", param);
        
        try {
            mongoRateLimiter.acquire();
            
            // 1. 构建查询条件
            Criteria criteria = buildCriteria(param);
            
            // 2. 获取总记录数
            String collectionName = getCollectionName(param.getRootNode());
            long total = mongoTemplate.count(new Query(criteria), UriEntity.class, collectionName);
            
            if (total == 0) {
                log.debug("No records found for query: {}", param);
                return new PageImpl<>(Collections.emptyList(), 
                    PageRequest.of(param.getPage() - 1, param.getSize()), 0);
            }
            
            // 3. 构建分页和排序
            Query query = new Query(criteria)
                .with(buildPageable(param))
                .with(buildSort(param));
            
            // 4. 如果只需要详情，则只查询必要字段
            if (Boolean.TRUE.equals(param.getOnlyDetail())) {
                query.fields()
                    .include("uri")
                    .include("details")
                    .include("update_time");
            }
            
            // 5. 执行查询
            List<UriEntity> entities = mongoTemplate.find(query, UriEntity.class, collectionName);
            
            // 6. 记录执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("Query completed in {}ms, found {} records", executionTime, entities.size());
            
            return new PageImpl<>(entities, 
                PageRequest.of(param.getPage() - 1, param.getSize()), total);
                
        } catch (Exception e) {
            log.error("Failed to query URIs by version: {}", param, e);
            throw new RuntimeException("Failed to query URIs by version", e);
        }
    }
    
    private Criteria buildCriteria(VersionQueryParam param) {
        Criteria criteria = Criteria.where("root_node").is(param.getRootNode())
            .and("uri_version").is(param.getVersion());
            
        // 处理软删除标记
        if (!Boolean.TRUE.equals(param.getIncludeDeleted())) {
            criteria.and("is_deleted").is(false);
        }
        
        return criteria;
    }
    
    private Pageable buildPageable(VersionQueryParam param) {
        return PageRequest.of(param.getPage() - 1, param.getSize());
    }
    
    private Sort buildSort(VersionQueryParam param) {
        if (StringUtils.hasText(param.getSortField())) {
            Sort.Direction direction = "desc".equalsIgnoreCase(param.getSortOrder()) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            return Sort.by(direction, param.getSortField());
        }
        return Sort.by(Sort.Direction.DESC, "update_time");
    }
    
    private String getCollectionName(String rootNode) {
        return String.format("%s_%s", CollectionConstants.URI_COLLECTION_PREFIX, rootNode);
    }
}
```

Controller层实现：

```java
package com.study.collect.business.testcase.controller;

@Slf4j
@RestController
@RequestMapping("/api/collect")
@RequiredArgsConstructor
@Validated
public class UriCollectController {
    private final UriCollectService collectService;
    
    @GetMapping("/version/{rootNode}/{version}")
    @ApiOperation("Get paginated URIs by version")
    public ResponseEntity<PageResponse<UriEntity>> getUrisByVersion(
            @PathVariable @NotBlank String rootNode,
            @PathVariable @NotBlank String version,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(1000) Integer size,
            @RequestParam(required = false) Boolean includeDeleted,
            @RequestParam(required = false) Boolean onlyDetail,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
            
        try {
            VersionQueryParam param = VersionQueryParam.builder()
                .rootNode(rootNode)
                .version(version)
                .page(page)
                .size(size)
                .includeDeleted(includeDeleted)
                .onlyDetail(onlyDetail)
                .sortField(sortField)
                .sortOrder(sortOrder)
                .build();
                
            Page<UriEntity> result = collectService.queryUrisByVersion(param);
            
            return ResponseEntity.ok(PageResponse.<UriEntity>builder()
                .code("200")
                .message("Success")
                .total(result.getTotalElements())
                .items(result.getContent())
                .page(page)
                .size(size)
                .totalPages(result.getTotalPages())
                .build());
                
        } catch (Exception e) {
            log.error("Failed to get URIs by version", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(PageResponse.<UriEntity>builder()
                    .code("500")
                    .message("Internal server error: " + e.getMessage())
                    .build());
        }
    }
}
```

在Repository层添加支持方法：

```java
package com.study.collect.business.testcase.repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UriRepository {
    private final MongoTemplate mongoTemplate;
    private final RateLimiter mongoRateLimiter;

    /**
     * 根据条件获取URI数据并支持分页
     */
    public Page<UriEntity> findByVersionWithPage(VersionQueryParam param) {
        try {
            mongoRateLimiter.acquire();
            
            String collectionName = getCollectionName(param.getRootNode());
            Criteria criteria = buildCriteria(param);
            
            // 获取总数
            long total = mongoTemplate.count(new Query(criteria), collectionName);
            
            if (total == 0) {
                return new PageImpl<>(Collections.emptyList(), 
                    PageRequest.of(param.getPage() - 1, param.getSize()), 0);
            }
            
            // 构建查询
            Query query = new Query(criteria)
                .with(PageRequest.of(param.getPage() - 1, param.getSize()));
                
            // 添加字段过滤
            if (Boolean.TRUE.equals(param.getOnlyDetail())) {
                query.fields()
                    .include("uri")
                    .include("details")
                    .include("update_time");
            }
            
            // 执行查询
            List<UriEntity> content = mongoTemplate.find(query, UriEntity.class, collectionName);
            
            return new PageImpl<>(content, 
                PageRequest.of(param.getPage() - 1, param.getSize()), total);
                
        } catch (Exception e) {
            log.error("Failed to find URIs by version with page: {}", param, e);
            throw new RuntimeException("Failed to find URIs", e);
        }
    }
    
    private Criteria buildCriteria(VersionQueryParam param) {
        return Criteria.where("root_node").is(param.getRootNode())
            .and("uri_version").is(param.getVersion())
            .and("is_deleted").is(false);
    }
}
```

这个实现的主要优化点：

1.  使用参数对象封装查询条件，便于扩展
2.  支持字段过滤，可以只返回必要字段
3.  支持自定义排序
4.  使用RateLimiter控制查询频率
5.  完善的日志记录
6.  统一的异常处理
7.  使用MongoDB的分页机制，避免内存分页
8.  支持软删除过滤

使用示例：

```java
// 基本查询
GET /api/collect/version/myRootNode/v1.0?page=1&size=20

// 包含已删除数据
GET /api/collect/version/myRootNode/v1.0?includeDeleted=true

// 只返回详情字段
GET /api/collect/version/myRootNode/v1.0?onlyDetail=true

// 自定义排序
GET /api/collect/version/myRootNode/v1.0?sortField=update_time&sortOrder=desc
```

这个实现保证了良好的性能和可扩展性，同时提供了灵活的查询选项。你可以根据实际需求进一步调整参数和返回字段。

