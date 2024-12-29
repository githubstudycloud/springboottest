// 测试数据生成请求
@Data
@Builder
public class TestDataGenerateRequest {
    private String versionId;                // 版本ID
    private Integer mainVersionCount;        // 主干版本数量
    private Integer branchCount;             // 分支数量
    private Integer testCaseCountPerVersion; // 每个版本的用例数量
    private Boolean generateDeleted;         // 是否生成已删除数据
    private String timeRange;                // 数据时间范围(如: 7d, 30d, 3m, 1y)
}

// 测试数据生成响应
@Data
@Builder
public class TestDataGenerateResponse {
    private String versionId;
    private int mainVersionCount;     // 生成的主干版本数
    private int branchCount;          // 生成的分支数
    private int totalTestCaseCount;   // 生成的总用例数
    private Date startTime;           // 数据开始时间
    private Date endTime;             // 数据结束时间
}

// 测试数据生成服务
@Service
@Slf4j 
public class TestDataGenerateService {
    
    @Autowired
    private TestCaseRepository testCaseRepository;
    
    @Autowired
    private VersionRepository versionRepository;
    
    private final Random random = new Random();
    
    /**
     * 生成测试数据
     */
    public TestDataGenerateResponse generateTestData(TestDataGenerateRequest request) {
        try {
            // 1. 解析时间范围
            Pair<Date, Date> timeRange = parseTimeRange(request.getTimeRange());
            Date startTime = timeRange.getLeft();
            Date endTime = timeRange.getRight();
            
            // 2. 生成版本数据
            String versionId = request.getVersionId();
            List<SubVersionInfo> mainVersions = generateMainVersions(versionId, 
                request.getMainVersionCount(), startTime, endTime);
            
            List<SubVersionInfo> branchVersions = generateBranchVersions(versionId,
                request.getBranchCount(), startTime, endTime);
            
            // 3. 保存版本信息
            VersionInfo versionInfo = VersionInfo.builder()
                .versionId(versionId)
                .versionName("Version-" + versionId)
                .mainVersions(mainVersions)
                .branchVersions(branchVersions)
                .build();
            
            versionRepository.save(versionInfo);
            
            // 4. 生成并保存测试用例数据
            int totalTestCases = generateTestCases(versionInfo, request.getTestCaseCountPerVersion(),
                startTime, endTime, request.getGenerateDeleted());
            
            // 5. 构建响应
            return TestDataGenerateResponse.builder()
                .versionId(versionId)
                .mainVersionCount(mainVersions.size())
                .branchCount(branchVersions.size())
                .totalTestCaseCount(totalTestCases)
                .startTime(startTime)
                .endTime(endTime)
                .build();
                
        } catch (Exception e) {
            log.error("Failed to generate test data", e);
            throw new RuntimeException("Failed to generate test data: " + e.getMessage());
        }
    }
    
    /**
     * 清理测试数据
     */
    public void clearTestData(String versionId) {
        try {
            versionRepository.deleteByVersionId(versionId);
            testCaseRepository.deleteByVersionId(versionId);
        } catch (Exception e) {
            log.error("Failed to clear test data", e);
            throw new RuntimeException("Failed to clear test data: " + e.getMessage());
        }
    }
    
    // 生成主干版本
    private List<SubVersionInfo> generateMainVersions(String versionId, int count, 
                                                    Date startTime, Date endTime) {
        List<SubVersionInfo> mainVersions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Date createTime = randomDate(startTime, endTime);
            mainVersions.add(SubVersionInfo.builder()
                .id(versionId + "-main-" + i)
                .name("Main-" + i)
                .type("MAIN")
                .metadata(generateVersionMetadata(createTime))
                .build());
        }
        return mainVersions;
    }
    
    // 生成分支版本
    private List<SubVersionInfo> generateBranchVersions(String versionId, int count,
                                                      Date startTime, Date endTime) {
        List<SubVersionInfo> branchVersions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Date createTime = randomDate(startTime, endTime);
            branchVersions.add(SubVersionInfo.builder()
                .id(versionId + "-branch-" + i)
                .name("Branch-" + i)
                .type("BRANCH")
                .metadata(generateVersionMetadata(createTime))
                .build());
        }
        return branchVersions;
    }
    
    // 生成测试用例
    private int generateTestCases(VersionInfo versionInfo, int countPerVersion,
                                Date startTime, Date endTime, boolean generateDeleted) {
        int totalCount = 0;
        List<TestCaseInfo> testCases = new ArrayList<>();
        
        // 为主干版本生成测试用例
        for (SubVersionInfo mainVersion : versionInfo.getMainVersions()) {
            testCases.addAll(generateVersionTestCases(versionInfo, mainVersion, 
                countPerVersion, startTime, endTime, generateDeleted));
            totalCount += countPerVersion;
        }
        
        // 为分支版本生成测试用例
        for (SubVersionInfo branchVersion : versionInfo.getBranchVersions()) {
            testCases.addAll(generateVersionTestCases(versionInfo, branchVersion,
                countPerVersion, startTime, endTime, generateDeleted));
            totalCount += countPerVersion;
        }
        
        // 批量保存测试用例
        testCaseRepository.saveAll(testCases);
        
        return totalCount;
    }
    
    // 为特定版本生成测试用例
    private List<TestCaseInfo> generateVersionTestCases(VersionInfo versionInfo,
                                                      SubVersionInfo subVersion,
                                                      int count, Date startTime,
                                                      Date endTime, boolean generateDeleted) {
        List<TestCaseInfo> testCases = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Date createTime = randomDate(startTime, endTime);
            Date updateTime = randomDate(createTime, endTime);
            
            boolean isDeleted = generateDeleted && random.nextDouble() < 0.1; // 10%的删除率
            
            testCases.add(TestCaseInfo.builder()
                .id(subVersion.getId() + "-case-" + i)
                .name("TestCase-" + i)
                .version(versionInfo)
                .subVersion(subVersion)
                .businessData(generateTestCaseData())
                .metadata(generateTestCaseMetadata(createTime, updateTime, isDeleted))
                .build());
        }
        return testCases;
    }
    
    // 生成版本元数据
    private Map<String, Object> generateVersionMetadata(Date createTime) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("createTime", createTime);
        metadata.put("creator", "test-generator");
        metadata.put("status", randomStatus());
        return metadata;
    }
    
    // 生成测试用例数据
    private Map<String, Object> generateTestCaseData() {
        Map<String, Object> data = new HashMap<>();
        data.put("priority", randomPriority());
        data.put("type", randomTestType());
        data.put("description", generateDescription());
        data.put("steps", generateTestSteps());
        return data;
    }
    
    // 生成测试用例元数据
    private Map<String, Object> generateTestCaseMetadata(Date createTime, 
                                                       Date updateTime,
                                                       boolean deleted) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("createTime", createTime);
        metadata.put("updateTime", updateTime);
        metadata.put("creator", "test-generator");
        metadata.put("deleted", deleted);
        return metadata;
    }
    
    // 工具方法：解析时间范围
    private Pair<Date, Date> parseTimeRange(String timeRange) {
        Pattern pattern = Pattern.compile("(\\d+)([dmny])");
        Matcher matcher = pattern.matcher(timeRange.toLowerCase());
        
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time range format");
        }
        
        int value = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);
        
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        
        switch (unit) {
            case "d": calendar.add(Calendar.DAY_OF_YEAR, -value); break;
            case "m": calendar.add(Calendar.MONTH, -value); break;
            case "y": calendar.add(Calendar.YEAR, -value); break;
            default: throw new IllegalArgumentException("Invalid time unit");
        }
        
        return Pair.of(calendar.getTime(), endTime);
    }
    
    // 工具方法：生成随机日期
    private Date randomDate(Date startInclusive, Date endInclusive) {
        long startMillis = startInclusive.getTime();
        long endMillis = endInclusive.getTime();
        long randomMillisSinceEpoch = startMillis + 
            (long) (random.nextDouble() * (endMillis - startMillis));
        
        return new Date(randomMillisSinceEpoch);
    }
    
    // 其他随机数据生成方法
    private String randomStatus() {
        String[] statuses = {"ACTIVE", "INACTIVE", "DEPRECATED"};
        return statuses[random.nextInt(statuses.length)];
    }
    
    private String randomPriority() {
        String[] priorities = {"P0", "P1", "P2", "P3"};
        return priorities[random.nextInt(priorities.length)];
    }
    
    private String randomTestType() {
        String[] types = {"FUNCTIONAL", "PERFORMANCE", "SECURITY", "UI"};
        return types[random.nextInt(types.length)];
    }
    
    private String generateDescription() {
        return "Generated test case description - " + UUID.randomUUID().toString();
    }
    
    private List<Map<String, String>> generateTestSteps() {
        List<Map<String, String>> steps = new ArrayList<>();
        int stepCount = 2 + random.nextInt(4); // 2-5步
        
        for (int i = 1; i <= stepCount; i++) {
            Map<String, String> step = new HashMap<>();
            step.put("step", "Step " + i);
            step.put("description", "Test step description " + i);
            step.put("expected", "Expected result " + i);
            steps.add(step);
        }
        
        return steps;
    }
}

// 测试数据生成控制器
@RestController
@RequestMapping("/api/test-data")
@Slf4j
public class TestDataController {

    @Autowired
    private TestDataGenerateService testDataGenerateService;
    
    /**
     * 生成测试数据
     */
    @PostMapping("/generate")
    public ResponseEntity<TestDataGenerateResponse> generateTestData(
            @RequestBody @Valid TestDataGenerateRequest request) {
        log.info("Generating test data for request: {}", request);
        TestDataGenerateResponse response = testDataGenerateService.generateTestData(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 清理测试数据
     */
    @DeleteMapping("/{versionId}")
    public ResponseEntity<Void> clearTestData(@PathVariable String versionId) {
        log.info("Clearing test data for version: {}", versionId);
        testDataGenerateService.clearTestData(versionId);
        return ResponseEntity.ok().build();
    }
}
