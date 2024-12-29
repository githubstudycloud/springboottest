// 领域模型
@Data
@Builder
public class TestCase {
    private String id;
    private String name;
    private String description;
    private Date createTime;
    private Date updateTime;
    private boolean deleted;
    private Map<String, Object> details;
}

// 采集参数
@Data
@Builder
public class TestCaseCollectParam {
    private String version;
    private Date startTime;
    private Date endTime;
    private boolean incremental;
}

// 采集器实现
@Slf4j
@Component
public class TestCaseCollector extends AbstractCollector<TestCaseCollectParam, List<TestCase>> {
    
    private static final int PAGE_SIZE = 500;
    private final HttpUtil httpUtil;
    
    @Value("${api.testcase.baseUrl}")
    private String baseUrl;
    
    @Autowired
    public TestCaseCollector(HttpUtil httpUtil) {
        this.httpUtil = httpUtil;
    }

    @Override
    protected CollectResult<List<TestCase>> doCollect(CollectContext<TestCaseCollectParam> context) {
        TestCaseCollectParam param = context.getParam();
        
        try {
            // 1. 获取用例总数
            int totalCount = getTotalCount(param.getVersion());
            log.info("Total test cases count: {}", totalCount);

            // 2. 获取用例ID列表
            Set<String> testCaseIds = collectTestCaseIds(param);
            log.info("Collected {} test case ids", testCaseIds.size());

            // 3. 如果是增量采集，获取删除的用例ID
            if (param.isIncremental()) {
                Set<String> deletedIds = collectDeletedIds(param);
                log.info("Collected {} deleted test case ids", deletedIds.size());
                // 将删除的ID也加入处理
                testCaseIds.addAll(deletedIds);
            }

            // 4. 批量获取用例详情
            List<TestCase> testCases = collectTestCaseDetails(testCaseIds);
            log.info("Successfully collected {} test cases", testCases.size());

            return CollectResult.success(testCases);
        } catch (Exception e) {
            log.error("Failed to collect test cases", e);
            return CollectResult.fail("Failed to collect test cases: " + e.getMessage());
        }
    }

    private int getTotalCount(String version) throws IOException {
        String url = baseUrl + "/api/testcase/count?version=" + version;
        HttpResponse response = httpUtil.get(url);
        if (response.getCode() != 200) {
            throw new CollectException("Failed to get total count");
        }
        JsonNode json = new ObjectMapper().readTree(response.getBody());
        return json.get("total").asInt();
    }

    private Set<String> collectTestCaseIds(TestCaseCollectParam param) throws IOException {
        Set<String> ids = new HashSet<>();
        int offset = 0;
        boolean hasMore = true;

        while (hasMore) {
            String url = String.format("%s/api/testcase/list?version=%s&offset=%d&limit=%d", 
                baseUrl, param.getVersion(), offset, PAGE_SIZE);
            
            // 增量采集时添加时间范围
            if (param.isIncremental()) {
                url += String.format("&startTime=%s&endTime=%s", 
                    formatDate(param.getStartTime()), formatDate(param.getEndTime()));
            }

            HttpResponse response = httpUtil.get(url);
            if (response.getCode() != 200) {
                throw new CollectException("Failed to get test case ids");
            }

            JsonNode json = new ObjectMapper().readTree(response.getBody());
            JsonNode items = json.get("items");
            if (items.size() == 0) {
                hasMore = false;
            } else {
                items.forEach(item -> ids.add(item.get("id").asText()));
                offset += PAGE_SIZE;
            }
        }

        return ids;
    }

    private Set<String> collectDeletedIds(TestCaseCollectParam param) throws IOException {
        Set<String> deletedIds = new HashSet<>();
        int offset = 0;
        boolean hasMore = true;

        while (hasMore) {
            String url = String.format("%s/api/testcase/deleted?startTime=%s&endTime=%s&offset=%d&limit=%d",
                baseUrl, formatDate(param.getStartTime()), formatDate(param.getEndTime()), 
                offset, PAGE_SIZE);

            HttpResponse response = httpUtil.get(url);
            if (response.getCode() != 200) {
                throw new CollectException("Failed to get deleted test case ids");
            }

            JsonNode json = new ObjectMapper().readTree(response.getBody());
            JsonNode items = json.get("items");
            if (items.size() == 0) {
                hasMore = false;
            } else {
                items.forEach(item -> deletedIds.add(item.get("id").asText()));
                offset += PAGE_SIZE;
            }
        }

        return deletedIds;
    }

    private List<TestCase> collectTestCaseDetails(Set<String> testCaseIds) {
        List<TestCase> testCases = new ArrayList<>();
        List<List<String>> batches = partition(testCaseIds, PAGE_SIZE);

        for (List<String> batch : batches) {
            try {
                List<CompletableFuture<TestCase>> futures = batch.stream()
                    .map(this::asyncCollectTestCaseDetail)
                    .collect(Collectors.toList());

                // 等待所有异步请求完成
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                // 收集结果
                futures.stream()
                    .map(CompletableFuture::join)
                    .forEach(testCases::add);

            } catch (Exception e) {
                log.error("Failed to collect test case details for batch", e);
                throw new CollectException("Failed to collect test case details", e);
            }
        }

        return testCases;
    }

    private CompletableFuture<TestCase> asyncCollectTestCaseDetail(String testCaseId) {
        String url = baseUrl + "/api/testcase/detail/" + testCaseId;
        return httpUtil.asyncGet(url)
            .thenApply(response -> {
                if (response.getCode() != 200) {
                    throw new CollectException("Failed to get test case detail: " + testCaseId);
                }
                return parseTestCaseDetail(response.getBody());
            });
    }

    private TestCase parseTestCaseDetail(String responseBody) {
        try {
            JsonNode json = new ObjectMapper().readTree(responseBody);
            return TestCase.builder()
                .id(json.get("id").asText())
                .name(json.get("name").asText())
                .description(json.get("description").asText())
                .createTime(parseDate(json.get("createTime").asText()))
                .updateTime(parseDate(json.get("updateTime").asText()))
                .deleted(json.get("deleted").asBoolean())
                .details(parseDetails(json.get("details")))
                .build();
        } catch (Exception e) {
            throw new CollectException("Failed to parse test case detail", e);
        }
    }

    // 工具方法
    private List<List<String>> partition(Set<String> ids, int size) {
        return new ArrayList<>(ids).stream()
            .collect(Collectors.groupingBy(i -> ids.size() / size))
            .values()
            .stream()
            .collect(Collectors.toList());
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
    }

    private Date parseDate(String dateStr) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(dateStr);
    }

    private Map<String, Object> parseDetails(JsonNode detailsNode) {
        Map<String, Object> details = new HashMap<>();
        detailsNode.fields().forEachRemaining(entry -> 
            details.put(entry.getKey(), entry.getValue().asText()));
        return details;
    }
}

// 服务层实现
@Service
@Slf4j
public class TestCaseCollectService {
    
    @Autowired
    private TestCaseCollector testCaseCollector;
    
    @Autowired
    private TestCaseRepository testCaseRepository;

    /**
     * 执行全量采集
     */
    public CollectResult<List<TestCase>> fullCollect(String version) {
        TestCaseCollectParam param = TestCaseCollectParam.builder()
            .version(version)
            .incremental(false)
            .build();

        CollectResult<List<TestCase>> result = testCaseCollector.collect(
            new CollectContext<>(param));

        if (result.isSuccess()) {
            // 保存采集结果
            testCaseRepository.saveAll(result.getData());
        }

        return result;
    }

    /**
     * 执行增量采集
     */
    public CollectResult<List<TestCase>> incrementalCollect(String version, 
                                                          Date startTime,
                                                          Date endTime) {
        TestCaseCollectParam param = TestCaseCollectParam.builder()
            .version(version)
            .startTime(startTime)
            .endTime(endTime)
            .incremental(true)
            .build();

        CollectResult<List<TestCase>> result = testCaseCollector.collect(
            new CollectContext<>(param));

        if (result.isSuccess()) {
            // 更新采集结果
            updateTestCases(result.getData());
        }

        return result;
    }

    private void updateTestCases(List<TestCase> testCases) {
        for (TestCase testCase : testCases) {
            if (testCase.isDeleted()) {
                testCaseRepository.deleteById(testCase.getId());
            } else {
                testCaseRepository.save(testCase);
            }
        }
    }
}
