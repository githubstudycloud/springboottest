// 采集范围控制参数
@Data
@Builder
public class VersionCollectScope {
    private boolean includeMain;  // 是否采集主干
    private Set<String> branchIds;  // 需要采集的分支ID集合，为空表示不采集任何分支
    
    public static VersionCollectScope onlyMain() {
        return VersionCollectScope.builder()
            .includeMain(true)
            .branchIds(Collections.emptySet())
            .build();
    }
    
    public static VersionCollectScope onlyBranches(Set<String> branchIds) {
        return VersionCollectScope.builder()
            .includeMain(false)
            .branchIds(branchIds)
            .build();
    }
    
    public static VersionCollectScope all() {
        return VersionCollectScope.builder()
            .includeMain(true)
            .branchIds(null)  // null 表示采集所有分支
            .build();
    }
    
    public boolean shouldCollectBranch(String branchId) {
        return branchIds == null || branchIds.contains(branchId);
    }
}

// 扩展采集参数
@Data
@Builder
public class VersionTestCaseCollectParam {
    private String versionId;
    private Date startTime;
    private Date endTime;
    private VersionCollectScope collectScope;
    private boolean incremental;
    
    @Builder.Default
    private int batchSize = 500;
    
    @Builder.Default
    private int maxConcurrent = 10;
}

// 采集策略接口
public interface CollectStrategy {
    CollectResult<List<TestCaseInfo>> collect(SubVersionInfo subVersion, 
                                            VersionTestCaseCollectParam param);
}

// 主干采集策略实现
@Component
@Slf4j
public class MainVersionCollectStrategy implements CollectStrategy {
    private final TestCaseCollector testCaseCollector;
    
    @Autowired
    public MainVersionCollectStrategy(TestCaseCollector testCaseCollector) {
        this.testCaseCollector = testCaseCollector;
    }
    
    @Override
    public CollectResult<List<TestCaseInfo>> collect(SubVersionInfo subVersion, 
                                                   VersionTestCaseCollectParam param) {
        // 主干版本采集实现
        log.info("Collecting main version test cases: {}", subVersion.getId());
        // ... 实现主干采集逻辑
        return CollectResult.success(Collections.emptyList());
    }
}

// 分支采集策略实现
@Component
@Slf4j
public class BranchVersionCollectStrategy implements CollectStrategy {
    private final TestCaseCollector testCaseCollector;
    
    @Autowired
    public BranchVersionCollectStrategy(TestCaseCollector testCaseCollector) {
        this.testCaseCollector = testCaseCollector;
    }
    
    @Override
    public CollectResult<List<TestCaseInfo>> collect(SubVersionInfo subVersion,
                                                   VersionTestCaseCollectParam param) {
        // 分支版本采集实现
        log.info("Collecting branch version test cases: {}", subVersion.getId());
        // ... 实现分支采集逻辑
        return CollectResult.success(Collections.emptyList());
    }
}

// 采集策略工厂
@Component
public class CollectStrategyFactory {
    private final Map<String, CollectStrategy> strategyMap = new HashMap<>();
    
    @Autowired
    public CollectStrategyFactory(MainVersionCollectStrategy mainStrategy,
                                BranchVersionCollectStrategy branchStrategy) {
        strategyMap.put("MAIN", mainStrategy);
        strategyMap.put("BRANCH", branchStrategy);
    }
    
    public CollectStrategy getStrategy(String type) {
        CollectStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported version type: " + type);
        }
        return strategy;
    }
}

// 改进后的版本测试用例服务
@Service
@Slf4j
public class VersionTestCaseService {
    private final VersionCollector versionCollector;
    private final CollectStrategyFactory strategyFactory;
    private final ExecutorService executorService;
    
    @Autowired
    public VersionTestCaseService(VersionCollector versionCollector,
                                CollectStrategyFactory strategyFactory,
                                TestCaseApiConfig apiConfig) {
        this.versionCollector = versionCollector;
        this.strategyFactory = strategyFactory;
        this.executorService = Executors.newFixedThreadPool(
            apiConfig.getTestCase().getMaxConcurrent());
    }
    
    public CollectResult<Map<String, List<TestCaseInfo>>> collectVersionTestCases(
            VersionTestCaseCollectParam param) {
        try {
            // 1. 获取版本信息
            CollectResult<VersionInfo> versionResult = versionCollector.collect(
                new CollectContext<>(param.getVersionId()));
                
            if (!versionResult.isSuccess()) {
                return CollectResult.fail(versionResult.getMessage());
            }
            
            VersionInfo versionInfo = versionResult.getData();
            Map<String, List<TestCaseInfo>> result = new ConcurrentHashMap<>();
            
            // 创建需要执行的采集任务
            List<CompletableFuture<Void>> collectTasks = new ArrayList<>();
            
            // 2. 根据采集范围处理主干版本
            if (param.getCollectScope().isIncludeMain() && !versionInfo.getMainVersions().isEmpty()) {
                collectTasks.add(CompletableFuture.runAsync(() ->
                    collectMainVersions(versionInfo.getMainVersions(), param, result),
                    executorService));
            }
            
            // 3. 根据采集范围处理分支版本
            if (!versionInfo.getBranchVersions().isEmpty()) {
                List<SubVersionInfo> branchesToCollect = versionInfo.getBranchVersions().stream()
                    .filter(branch -> param.getCollectScope().shouldCollectBranch(branch.getId()))
                    .collect(Collectors.toList());
                    
                if (!branchesToCollect.isEmpty()) {
                    collectTasks.add(CompletableFuture.runAsync(() ->
                        collectBranchVersions(branchesToCollect, param, result),
                        executorService));
                }
            }
            
            // 4. 等待所有采集任务完成
            if (!collectTasks.isEmpty()) {
                CompletableFuture.allOf(collectTasks.toArray(new CompletableFuture[0])).join();
            }
            
            return CollectResult.success(result);
        } catch (Exception e) {
            log.error("Failed to collect version test cases: {}", param.getVersionId(), e);
            return CollectResult.fail("Failed to collect version test cases: " + e.getMessage());
        }
    }
    
    private void collectMainVersions(List<SubVersionInfo> mainVersions,
                                   VersionTestCaseCollectParam param,
                                   Map<String, List<TestCaseInfo>> result) {
        CollectStrategy strategy = strategyFactory.getStrategy("MAIN");
        for (SubVersionInfo mainVersion : mainVersions) {
            try {
                CollectResult<List<TestCaseInfo>> collectResult = 
                    strategy.collect(mainVersion, param);
                if (collectResult.isSuccess()) {
                    result.put(mainVersion.getId(), collectResult.getData());
                } else {
                    log.error("Failed to collect main version: {}", mainVersion.getId());
                }
            } catch (Exception e) {
                log.error("Error collecting main version: {}", mainVersion.getId(), e);
            }
        }
    }
    
    private void collectBranchVersions(List<SubVersionInfo> branches,
                                     VersionTestCaseCollectParam param,
                                     Map<String, List<TestCaseInfo>> result) {
        CollectStrategy strategy = strategyFactory.getStrategy("BRANCH");
        List<List<SubVersionInfo>> batchedBranches = 
            Lists.partition(branches, param.getBatchSize());
            
        for (List<SubVersionInfo> batchBranches : batchedBranches) {
            List<CompletableFuture<Void>> batchTasks = batchBranches.stream()
                .map(branch -> CompletableFuture.runAsync(() -> {
                    try {
                        CollectResult<List<TestCaseInfo>> collectResult = 
                            strategy.collect(branch, param);
                        if (collectResult.isSuccess()) {
                            result.put(branch.getId(), collectResult.getData());
                        } else {
                            log.error("Failed to collect branch: {}", branch.getId());
                        }
                    } catch (Exception e) {
                        log.error("Error collecting branch: {}", branch.getId(), e);
                    }
                }, executorService))
                .collect(Collectors.toList());
                
            CompletableFuture.allOf(batchTasks.toArray(new CompletableFuture[0])).join();
        }
    }
}

// 使用示例
@Service
public class CollectService {
    @Autowired
    private VersionTestCaseService versionTestCaseService;
    
    public void collectSpecificVersions() {
        // 只采集主干
        VersionTestCaseCollectParam mainOnlyParam = VersionTestCaseCollectParam.builder()
            .versionId("v1.0")
            .startTime(new Date())
            .endTime(new Date())
            .collectScope(VersionCollectScope.onlyMain())
            .build();
            
        // 只采集指定分支
        Set<String> branchIds = Set.of("branch1", "branch2");
        VersionTestCaseCollectParam branchesParam = VersionTestCaseCollectParam.builder()
            .versionId("v1.0")
            .startTime(new Date())
            .endTime(new Date())
            .collectScope(VersionCollectScope.onlyBranches(branchIds))
            .build();
            
        // 采集主干和指定分支
        VersionTestCaseCollectParam mixedParam = VersionTestCaseCollectParam.builder()
            .versionId("v1.0")
            .startTime(new Date())
            .endTime(new Date())
            .collectScope(VersionCollectScope.builder()
                .includeMain(true)
                .branchIds(branchIds)
                .build())
            .build();
            
        // 执行采集
        CollectResult<Map<String, List<TestCaseInfo>>> result = 
            versionTestCaseService.collectVersionTestCases(mixedParam);
    }
}
