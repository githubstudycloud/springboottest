// UriHttpService.java 修改
@Slf4j
@Service
@RequiredArgsConstructor
public class UriHttpService {
    // ... other code remains the same ...

    /**
     * 获取单页版本列表
     */
    public CompletableFuture<PageResponse<VersionInfo>> getVersionsPage(
            String serverUrl, String rootNode, int page, int size) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUrl + "/api/versions",
                        String.format(
                                "{\"rootNode\":\"%s\",\"page\":%d,\"size\":%d}",
                                rootNode, page, size
                        )
                ).getBody();

                return versionParser.parse(response);
            } catch (Exception e) {
                log.error("Failed to get versions page for rootNode: {}, page: {}", rootNode, page, e);
                throw new RuntimeException("Failed to get versions page", e);
            }
        }, httpExecutor);
    }
}

// UriCollectService.java 新增方法
public interface UriCollectService {
    // ... existing methods ...

    /**
     * 获取所有版本信息
     */
    List<VersionInfo> getAllVersions(String rootNode, String serverUrl) throws ExecutionException, InterruptedException;
}

// UriCollectServiceImpl.java 实现新方法
@Slf4j
@Service
@RequiredArgsConstructor
public class UriCollectServiceImpl implements UriCollectService {
    // ... existing code ...

    @Override
    public List<VersionInfo> getAllVersions(String rootNode, String serverUrl) 
            throws ExecutionException, InterruptedException {
        List<VersionInfo> allVersions = new ArrayList<>();
        int page = 1;
        int pageSize = 100; // 较大的页面大小以减少请求次数
        
        while (true) {
            PageResponse<VersionInfo> response = httpService.getVersionsPage(serverUrl, rootNode, page, pageSize).get();
            
            if (response.getItems() == null || response.getItems().isEmpty()) {
                break;
            }
            
            allVersions.addAll(response.getItems());
            
            // 如果已经获取所有数据，退出循环
            if (allVersions.size() >= response.getTotal()) {
                break;
            }
            
            page++;
        }
        
        return allVersions;
    }

    private void processCollectTask(CollectParam param) {
        String taskId = param.getTaskId();
        taskManager.updateTaskStatus(taskId, "PROCESSING", "Starting collection");

        try {
            // 使用新方法获取所有版本
            List<VersionInfo> versions = getAllVersions(param.getRootNode(), param.getServerUrl());

            if (StringUtils.hasText(param.getVersion())) {
                versions = versions.stream()
                        .filter(v -> v.getVersion().equals(param.getVersion()))
                        .toList();
            }

            long totalUris = 0;
            for (VersionInfo version : versions) {
                // 获取该版本下的URI数量
                int versionUriCount = httpService.getUriCount(param.getServerUrl(), version.getVersion()).get();
                totalUris += versionUriCount;

                // 处理该版本的URI
                processVersionUris(param, version, versionUriCount, taskId);
            }

            taskManager.updateTaskStatus(taskId, "COMPLETED", "Collection completed successfully");

        } catch (Exception e) {
            log.error("Failed to process collect task: {}", taskId, e);
            taskManager.updateTaskStatus(taskId, "ERROR", "Collection failed: " + e.getMessage());
            throw new RuntimeException("Failed to process collect task", e);
        }
    }
}

// CollectScheduler.java 修改版本获取逻辑
@Slf4j
@Component
@RequiredArgsConstructor
public class CollectScheduler {
    // ... existing code ...

    private void checkRootNode(String rootNode) throws ExecutionException, InterruptedException {
        // 使用 collectService 获取所有版本
        List<VersionInfo> versions = collectService.getAllVersions(rootNode, serverUrl);

        if (versions.isEmpty()) {
            log.warn("No versions found for rootNode: {}", rootNode);
            return;
        }

        // 检查每个版本
        for (VersionInfo version : versions) {
            try {
                checkVersion(rootNode, version);
            } catch (Exception e) {
                log.error("Failed to check version: {} for rootNode: {}",
                        version.getVersion(), rootNode, e);
            }
        }
    }
}
