// 基础数据模型
@Data
@Builder
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
}

@Data
@Builder
public class PageResponse<T> {
    private List<T> items;
    private int total;
    private int offset;
    private int limit;
}

@Data
@Builder
public class VersionData {
    private String versionId;
    private String versionName;
    private List<SubVersionData> mainVersions;  // 主干小版本
    private List<SubVersionData> branchVersions; // 分支小版本
}

@Data
@Builder
public class SubVersionData {
    private String id;
    private String name;
    private String parentId;
    private Date createTime;
    private Date updateTime;
}

@Data
@Builder
public class TestCaseData {
    private String id;
    private String name;
    private String versionId;
    private String subVersionId;
    private Map<String, Object> details;
    private Date createTime;
    private Date updateTime;
    private boolean deleted;
}

// 业务模型
@Data
@Builder
public class VersionInfo {
    private String versionId;
    private String versionName;
    private List<SubVersionInfo> mainVersions;
    private List<SubVersionInfo> branchVersions;
    private Map<String, Object> extendInfo;
}

@Data
@Builder
public class SubVersionInfo {
    private String id;
    private String name;
    private String type; // MAIN or BRANCH
    private Map<String, Object> metadata;
}

@Data
@Builder
public class TestCaseInfo {
    private String id;
    private String name;
    private VersionInfo version;
    private SubVersionInfo subVersion;
    private Map<String, Object> businessData;
    private Map<String, Object> metadata;
}

// 配置模型
@ConfigurationProperties(prefix = "testcase.api")
@Data
public class TestCaseApiConfig {
    private String baseUrl;
    private ApiConfig version;
    private ApiConfig testCase;
    
    @Data
    public static class ApiConfig {
        private String listPath;
        private String detailPath;
        private Map<String, String> params;
        private int pageSize = 500;
        private int maxConcurrent = 10;
    }
}

// URL构建器
@Component
@Slf4j
public class ApiUrlBuilder {
    private final TestCaseApiConfig config;
    
    public ApiUrlBuilder(TestCaseApiConfig config) {
        this.config = config;
    }
    
    public String buildUrl(String apiType, String path, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(config.getBaseUrl())
            .path(path);
            
        // 添加配置中的默认参数
        ApiConfig apiConfig = getApiConfig(apiType);
        if (apiConfig != null && apiConfig.getParams() != null) {
            apiConfig.getParams().forEach(builder::queryParam);
        }
        
        // 添加自定义参数
        if (params != null) {
            params.forEach(builder::queryParam);
        }
        
        return builder.build().encode().toUriString();
    }
    
    private ApiConfig getApiConfig(String apiType) {
        if ("version".equals(apiType)) {
            return config.getVersion();
        } else if ("testCase".equals(apiType)) {
            return config.getTestCase();
        }
        return null;
    }
}

// 版本采集器
@Slf4j
@Component
public class VersionCollector extends AbstractCollector<String, VersionInfo> {
    
    private final HttpUtil httpUtil;
    private final ApiUrlBuilder urlBuilder;
    private final TestCaseApiConfig apiConfig;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public VersionCollector(HttpUtil httpUtil, ApiUrlBuilder urlBuilder, 
                          TestCaseApiConfig apiConfig, ObjectMapper objectMapper) {
        this.httpUtil = httpUtil;
        this.urlBuilder = urlBuilder;
        this.apiConfig = apiConfig;
        this.objectMapper = objectMapper;
    }

    @Override
    protected CollectResult<VersionInfo> doCollect(CollectContext<String> context) {
        String versionId = context.getParam();
        try {
            // 获取版本信息
            VersionData versionData = collectVersionData(versionId);
            
            // 并发获取主干和分支的小版本信息
            CompletableFuture<List<SubVersionInfo>> mainVersionsFuture = 
                asyncCollectSubVersions(versionData.getMainVersions(), "MAIN");
            CompletableFuture<List<SubVersionInfo>> branchVersionsFuture = 
                asyncCollectSubVersions(versionData.getBranchVersions(), "BRANCH");
            
            // 等待所有异步操作完成
            CompletableFuture.allOf(mainVersionsFuture, branchVersionsFuture).join();
            
            // 构建业务模型
            VersionInfo versionInfo = VersionInfo.builder()
                .versionId(versionData.getVersionId())
                .versionName(versionData.getVersionName())
                .mainVersions(mainVersionsFuture.get())
                .branchVersions(branchVersionsFuture.get())
                .build();
                
            return CollectResult.success(versionInfo);
        } catch (Exception e) {
            log.error("Failed to collect version info: {}", versionId, e);
            return CollectResult.fail("Failed to collect version info: " + e.getMessage());
        }
    }
    
    private VersionData collectVersionData(String versionId) throws IOException {
        String url = urlBuilder.buildUrl("version", apiConfig.getVersion().getListPath(),
            Map.of("versionId", versionId));
            
        HttpResponse response = httpUtil.get(url);
        if (response.getCode() != 200) {
            throw new CollectException("Failed to get version data");
        }
        
        return objectMapper.readValue(response.getBody(), VersionData.class);
    }
    
    private CompletableFuture<List<SubVersionInfo>> asyncCollectSubVersions(
            List<SubVersionData> subVersions, String type) {
            
        if (subVersions == null || subVersions.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        
        // 分批处理
        List<List<SubVersionData>> batches = Lists.partition(subVersions, 
            apiConfig.getVersion().getPageSize());
            
        // 创建所有批次的异步任务
        List<CompletableFuture<List<SubVersionInfo>>> batchFutures = batches.stream()
            .map(batch -> CompletableFuture.supplyAsync(() -> 
                collectSubVersionBatch(batch, type)))
            .collect(Collectors.toList());
            
        // 合并所有批次结果
        return CompletableFuture.allOf(
            batchFutures.toArray(new CompletableFuture[0]))
            .thenApply(v -> batchFutures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList()));
    }
    
    private List<SubVersionInfo> collectSubVersionBatch(List<SubVersionData> batch, String type) {
        return batch.stream()
            .map(data -> convertToSubVersionInfo(data, type))
            .collect(Collectors.toList());
    }
    
    private SubVersionInfo convertToSubVersionInfo(SubVersionData data, String type) {
        return SubVersionInfo.builder()
            .id(data.getId())
            .name(data.getName())
            .type(type)
            .metadata(buildMetadata(data))
            .build();
    }
    
    private Map<String, Object> buildMetadata(SubVersionData data) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("parentId", data.getParentId());
        metadata.put("createTime", data.getCreateTime());
        metadata.put("updateTime", data.getUpdateTime());
        return metadata;
    }
}

// 测试用例采集服务
@Service
@Slf4j
public class VersionTestCaseService {
    
    private final VersionCollector versionCollector;
    private final TestCaseCollector testCaseCollector;
    private final ExecutorService executorService;
    
    @Autowired
    public VersionTestCaseService(VersionCollector versionCollector,
                                TestCaseCollector testCaseCollector,
                                TestCaseApiConfig apiConfig) {
        this.versionCollector = versionCollector;
        this.testCaseCollector = testCaseCollector;
        this.executorService = Executors.newFixedThreadPool(
            apiConfig.getTestCase().getMaxConcurrent());
    }
    
    public CollectResult<Map<String, List<TestCaseInfo>>> collectVersionTestCases(
            String versionId, Date startTime, Date endTime) {
        try {
            // 1. 获取版本信息
            CollectResult<VersionInfo> versionResult = versionCollector.collect(
                new CollectContext<>(versionId));
                
            if (!versionResult.isSuccess()) {
                return CollectResult.fail(versionResult.getMessage());
            }
            
            VersionInfo versionInfo = versionResult.getData();
            
            // 2. 并发采集主干和分支的测试用例
            Map<String, List<TestCaseInfo>> result = new ConcurrentHashMap<>();
            
            // 主干版本采集
            CompletableFuture<Void> mainFuture = CompletableFuture.runAsync(() -> 
                collectMainVersionTestCases(versionInfo, startTime, endTime, result),
                executorService);
                
            // 分支版本采集
            CompletableFuture<Void> branchFuture = CompletableFuture.runAsync(() ->
                collectBranchVersionTestCases(versionInfo, startTime, endTime, result),
                executorService);
                
            // 等待所有采集完成
            CompletableFuture.allOf(mainFuture, branchFuture).join();
            
            return CollectResult.success(result);
        } catch (Exception e) {
            log.error("Failed to collect version test cases: {}", versionId, e);
            return CollectResult.fail("Failed to collect version test cases: " + e.getMessage());
        }
    }
    
    private void collectMainVersionTestCases(VersionInfo versionInfo, 
            Date startTime, Date endTime, Map<String, List<TestCaseInfo>> result) {
        // 主干版本测试用例采集实现
    }
    
    private void collectBranchVersionTestCases(VersionInfo versionInfo,
            Date startTime, Date endTime, Map<String, List<TestCaseInfo>> result) {
        // 分支版本测试用例采集实现
    }
}

// 配置文件示例 (application.yml)
/*
testcase:
  api:
    baseUrl: http://api.example.com
    version:
      listPath: /api/version/list
      detailPath: /api/version/detail
      pageSize: 500
      maxConcurrent: 10
      params:
        type: testcase
        format: json
    testCase:
      listPath: /api/testcase/list
      detailPath: /api/testcase/detail
      pageSize: 500
      maxConcurrent: 10
      params:
        status: active
*/
