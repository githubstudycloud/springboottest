private void processVersionUris(CollectParam param, VersionInfo version, int totalCount, String taskId) {
    try {
        int processedCount = 0;
        int offset = 0;
        int batchSize = param.getBatchSize() != null ? param.getBatchSize() : HTTP_BATCH_SIZE;

        // 先获取所有URI列表
        List<String> allUris = new ArrayList<>();
        while (offset < totalCount) {
            // 获取一批URI
            List<String> uris = httpService.getUriList(
                param.getServerUrl(), 
                version.getVersion(),
                offset,
                Math.min(batchSize, totalCount - offset)
            ).get();
            
            if (uris.isEmpty()) {
                break;
            }
            
            allUris.addAll(uris);
            offset += uris.size();
            
            // 更新任务进度
            taskManager.updateTaskProgress(taskId, processedCount, totalCount);
        }
        
        // 对获取到的URI列表进行分批处理
        List<List<String>> uriBatches = Lists.partition(allUris, HTTP_BATCH_SIZE);
        for (List<String> batch : uriBatches) {
            try {
                // 获取URI详情
                List<UriDetail> details = httpService.getUriDetails(param.getServerUrl(), batch).get();
                
                // 将详情转换为实体并保存
                List<UriEntity> entities = new ArrayList<>();
                for (UriDetail detail : details) {
                    try {
                        UriEntity entity = entityPool.borrowObject();
                        try {
                            fillEntity(entity, param.getRootNode(), version, detail);
                            entities.add(entity);
                        } catch (Exception e) {
                            log.error("Failed to fill entity for URI: {}", detail.getUri(), e);
                            entityPool.returnObject(entity);
                        }
                    } catch (Exception e) {
                        log.error("Failed to borrow entity from pool for URI: {}", detail.getUri(), e);
                    }
                }

                if (!entities.isEmpty()) {
                    try {
                        uriRepository.batchUpsert(param.getRootNode(), entities);
                    } finally {
                        // 返还实体到对象池
                        for (UriEntity entity : entities) {
                            try {
                                entityPool.returnObject(entity);
                            } catch (Exception e) {
                                log.error("Failed to return entity to pool", e);
                            }
                        }
                    }
                }

                // 更新处理进度
                processedCount += batch.size();
                taskManager.updateTaskProgress(taskId, processedCount, totalCount);
                
            } catch (Exception e) {
                log.error("Failed to process URI batch of size {}", batch.size(), e);
                if (!param.isAllowDuplicate()) {
                    throw e;
                }
            }
        }

        // 如果所有URI都处理完成，执行清理操作
        if (processedCount >= totalCount) {
            cleanupUriData(param, allUris);
        }

    } catch (Exception e) {
        log.error("Failed to process version: {}", version.getVersion(), e);
        throw new RuntimeException("Failed to process version", e);
    }
}

private void cleanupUriData(CollectParam param, List<String> allUris) {
    if (!param.isHardDelete() && !param.isForceUpdate()) {
        return;
    }

    try {
        UriCleanupService cleanupService = new UriCleanupService(uriRepository);
        cleanupService.cleanupUriDataAsync(
            allUris,
            param.getRootNode(),
            param.getVersion(),
            param.isHardDelete()
        );
    } catch (Exception e) {
        log.error("Failed to cleanup URI data", e);
    }
}

// 在UriHttpService中添加新的方法
@Service
public class UriHttpService {
    // ... 其他代码 ...

    /**
     * 分页获取URI列表
     */
    public CompletableFuture<List<String>> getUriList(
            String serverUrl, 
            String version,
            int offset,
            int limit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                    serverUrl + "/api/uris",
                    String.format(
                        "{\"version\":\"%s\",\"offset\":%d,\"limit\":%d}",
                        version, offset, limit
                    )
                ).getBody();

                return uriListParser.parse(response);
            } catch (Exception e) {
                log.error("Failed to get URI list for version: {}, offset: {}, limit: {}", 
                    version, offset, limit, e);
                throw new RuntimeException("Failed to get URI list", e);
            }
        }, httpExecutor);
    }
}
