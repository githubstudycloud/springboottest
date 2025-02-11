// MockDataController.java
@RestController
@RequestMapping("/api")
@Slf4j
public class MockDataController {
    private final MockDataService mockDataService;

    public MockDataController(MockDataService mockDataService) {
        this.mockDataService = mockDataService;
    }

    @PostMapping("/versions")
    public ResponseEntity<ApiResponse<List<VersionInfo>>> getVersions(@RequestBody VersionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
            mockDataService.getVersions(request.getRootNode(), request.getPage(), request.getSize())
        ));
    }

    @PostMapping("/uris")
    public ResponseEntity<ApiResponse<List<String>>> getUris(@RequestBody UriRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
            mockDataService.getUris(request.getVersion())
        ));
    }

    @PostMapping("/uri/count")
    public ResponseEntity<ApiResponse<Integer>> getUriCount(@RequestBody UriRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
            mockDataService.getUriCount(request.getVersion())
        ));
    }

    @PostMapping("/details")
    public ResponseEntity<ApiResponse<List<UriDetail>>> getUriDetails(@RequestBody UriDetailRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
            mockDataService.getUriDetails(request.getUris())
        ));
    }
}

// MockDataService.java
@Service
@Slf4j
public class MockDataService {
    private static final Random RANDOM = new Random();
    private final Map<String, List<VersionInfo>> versionCache = new ConcurrentHashMap<>();
    private final Map<String, List<String>> uriCache = new ConcurrentHashMap<>();
    private final Map<String, Map<String, UriDetail>> detailCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 初始化模拟数据
        generateMockData();
    }

    public List<VersionInfo> getVersions(String rootNode, int page, int size) {
        List<VersionInfo> versions = versionCache.getOrDefault(rootNode, new ArrayList<>());
        int start = (page - 1) * size;
        int end = Math.min(start + size, versions.size());
        return versions.subList(start, end);
    }

    public List<String> getUris(String version) {
        return uriCache.getOrDefault(version, new ArrayList<>());
    }

    public int getUriCount(String version) {
        return uriCache.getOrDefault(version, new ArrayList<>()).size();
    }

    public List<UriDetail> getUriDetails(List<String> uris) {
        return uris.stream()
            .map(uri -> {
                String version = extractVersion(uri);
                Map<String, UriDetail> versionDetails = detailCache.getOrDefault(version, new HashMap<>());
                return versionDetails.getOrDefault(uri, generateUriDetail(uri));
            })
            .collect(Collectors.toList());
    }

    private void generateMockData() {
        // 生成版本数据
        List<String> rootNodes = Arrays.asList("app1", "app2", "app3");
        for (String rootNode : rootNodes) {
            List<VersionInfo> versions = generateVersions();
            versionCache.put(rootNode, versions);

            // 为每个版本生成URI数据
            for (VersionInfo version : versions) {
                List<String> uris = generateUris(version.getVersion(), 1000);
                uriCache.put(version.getVersion(), uris);

                // 生成URI详情
                Map<String, UriDetail> details = new HashMap<>();
                for (String uri : uris) {
                    details.put(uri, generateUriDetail(uri));
                }
                detailCache.put(version.getVersion(), details);
            }
        }
    }

    private List<VersionInfo> generateVersions() {
        List<VersionInfo> versions = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            versions.add(VersionInfo.builder()
                .version("v" + i)
                .name("Version " + i)
                .type(i % 2 == 0 ? "BRANCH" : "TRUNK")
                .updateTime(LocalDateTime.now().minusDays(i))
                .build());
        }
        return versions;
    }

    private List<String> generateUris(String version, int count) {
        List<String> uris = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            uris.add(String.format("/api/%s/resource/%d", version, i));
        }
        return uris;
    }

    private UriDetail generateUriDetail(String uri) {
        return UriDetail.builder()
            .uri(uri)
            .realUri(uri.replace("/api", "/real"))
            .number(String.valueOf(RANDOM.nextInt(1000)))
            .name("Resource " + RANDOM.nextInt(100))
            .updateTime(LocalDateTime.now().minusHours(RANDOM.nextInt(24)))
            .details(generateRandomDetails())
            .build();
    }

    private Map<String, Object> generateRandomDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("status", RANDOM.nextBoolean() ? "active" : "inactive");
        details.put("priority", RANDOM.nextInt(5));
        details.put("category", "category-" + RANDOM.nextInt(10));
        return details;
    }

    private String extractVersion(String uri) {
        return uri.split("/")[2];
    }
}

// ApiResponse.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String code;
    private String message;
    private T result;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("200", "Success", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("500", message, null);
    }
}

// application.yml
server:
  port: 8080
  tomcat:
    threads:
      max: 200
    max-connections: 8192
    accept-count: 100
    
spring:
  application:
    name: mock-data-service
