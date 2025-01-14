// application.yml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/your_database

// MongoConfig.java
@Configuration
public class MongoConfig {
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "your_database");
    }
}

// MongoService.java
@Service
@Slf4j
public class MongoService {
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public List<Map<String, Object>> queryAllUriCollections() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取所有以uri_开头的集合名
        Set<String> collectionNames = mongoTemplate.getCollectionNames()
            .stream()
            .filter(name -> name.startsWith("uri_"))
            .collect(Collectors.toSet());
            
        // 遍历每个集合进行查询
        for (String collectionName : collectionNames) {
            Query query = new Query(Criteria.where("deleted").is(false));
            
            List<Map> documents = mongoTemplate.find(query, Map.class, collectionName);
            
            if (!documents.isEmpty()) {
                Map<String, Object> collectionResult = new HashMap<>();
                collectionResult.put("collection", collectionName);
                collectionResult.put("documents", documents);
                result.add(collectionResult);
            }
            
            log.info("Collection: {}, Found {} documents", collectionName, documents.size());
        }
        
        return result;
    }
}

// Controller.java
@RestController
@RequestMapping("/api")
public class MongoController {
    @Autowired
    private MongoService mongoService;
    
    @GetMapping("/query-uri-collections")
    public ResponseEntity<List<Map<String, Object>>> queryUriCollections() {
        List<Map<String, Object>> result = mongoService.queryAllUriCollections();
        return ResponseEntity.ok(result);
    }
}

// pom.xml dependencies
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
