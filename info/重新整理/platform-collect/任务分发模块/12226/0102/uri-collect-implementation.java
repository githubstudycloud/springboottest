// UriEntity.java
@Document(collection = "uri_collect")
@Data
@EqualsAndHashCode(callSuper = true)
public class UriEntity extends VersionEntity {
    private String uri;                    // Original URI
    private String rootNode;               // Root node identifier
    private String versionType;            // TRUNK or BRANCH
    private Map<String, Object> details;   // URI details
    
    @Indexed
    private String uriHash;                // Hashed URI for efficient lookup
    
    @Override
    public void prePersist() {
        super.prePersist();
        if (this.uriHash == null) {
            this.uriHash = HashUtil.hash(this.uri);
        }
    }
}

// UriRepository.java
@Repository
public interface UriRepository extends BaseMongoRepository<UriEntity> {
    List<UriEntity> findByRootNodeAndVersionType(String rootNode, String versionType);
    List<UriEntity> findByRootNodeAndVersionTypeAndVersionCodeGreaterThan(
            String rootNode, String versionType, String versionCode);
    void deleteByUriHashNotIn(Collection<String> uriHashes);
}

// UriHttpService.java
@Service
@Slf4j
public class UriHttpService {
    private final HttpUtil httpUtil;
    private final ObjectMapper objectMapper;
    
    public List<String> getVersions(String rootNode) throws IOException {
        String response = httpUtil.post("/api/versions", 
            objectMapper.writeValueAsString(Map.of("rootNode", rootNode)))
            .getBody();
        return objectMapper.readValue(response, new TypeReference<>() {});
    }
    
    public List<String> getUriList(String version, TimeRangeParam timeRange) throws IOException {
        String response = httpUtil.post("/api/uris", 
            objectMapper.writeValueAsString(Map.of(
                "version", version,
                "startTime", timeRange.getStartTime(),
                "endTime", timeRange.getEndTime())))
            .getBody();
        return objectMapper.readValue(response, new TypeReference<>() {});
    }
    
    public List<Map<String, Object>> getUriDetails(List<String> uris) throws IOException {
        String response = httpUtil.post("/api/details",
            objectMapper.writeValueAsString(Map.of("uris", uris)))
            .getBody();
        return objectMapper.readValue(response, new TypeReference<>() {});
    }
}

// UriCollectServiceImpl.java
@Service
@Slf4j
public class UriCollectServiceImpl implements UriCollectService {
    private final UriHttpService httpService;
    private final UriRepository repository;
    private final ExecutorService executorService;
    
    private static final int BATCH_SIZE = 200;
    
    @Override
    public void syncData(String rootNode, String version, boolean incremental) {
        // 1. Get all versions first
        List<String> versions = httpService.getVersions(rootNode);
        
        // 2. For cleanup in incremental mode, get all URIs first
        if (incremental) {
            Set<String> allUris = new HashSet<>();
            CompletableFuture.allOf(
                versions.stream()
                    .map(v -> CompletableFuture.runAsync(() -> {
                        List<String> uris = httpService.getUriList(v, null);
                        allUris.addAll(uris.stream()
                            .map(HashUtil::hash)
                            .collect(Collectors.toSet()));
                    }, executorService))
                    .toArray(CompletableFuture[]::new)
            ).join();
            
            // Delete URIs not in the current set
            repository.deleteByUriHashNotIn(allUris);
        }
        
        // 3. Process each version in parallel
        CompletableFuture.allOf(
            versions.stream()
                .map(v -> CompletableFuture.runAsync(() -> 
                    processVersion(rootNode, v, incremental), executorService))
                .toArray(CompletableFuture[]::new)
        ).join();
    }
    
    private void processVersion(String rootNode, String version, boolean incremental) {
        TimeRangeParam timeRange = incremental ? 
            TimeRangeParam.getDefaultRange() : null;
        
        List<String> uris = httpService.getUriList(version, timeRange);
        
        // Process URIs in batches
        Lists.partition(uris, BATCH_SIZE)
            .parallelStream()
            .forEach(batch -> {
                List<Map<String, Object>> details = httpService.getUriDetails(batch);
                List<UriEntity> entities = details.stream()
                    .map(detail -> createEntity(rootNode, version, detail))
                    .collect(Collectors.toList());
                repository.saveAll(entities);
            });
    }
}

// UriCollectController.java
@RestController
@RequestMapping("/api/collect")
@Slf4j
public class UriCollectController {
    private final UriCollectService collectService;
    
    @PostMapping("/sync")
    public ResponseEntity<Void> syncData(
            @RequestParam String rootNode,
            @RequestParam(required = false) String version,
            @RequestParam(defaultValue = "false") boolean incremental) {
        collectService.syncData(rootNode, version, incremental);
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
