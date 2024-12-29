// 测试数据模板配置
@Data
@Builder
public class TestDataTemplate {
    private String templateName;
    private Map<String, Object> versionTemplate;    // 版本数据模板
    private Map<String, Object> testCaseTemplate;   // 用例数据模板
    private List<String> customFields;              // 自定义字段
    private Map<String, List<String>> enumValues;   // 枚举值配置
}

// 高级生成选项
@Data
@Builder
public class AdvancedGenerateOptions {
    @Builder.Default
    private boolean generateRelations = false;     // 是否生成关联关系
    
    @Builder.Default
    private boolean generateTags = false;          // 是否生成标签
    
    @Builder.Default
    private boolean generateAttachments = false;   // 是否生成附件信息
    
    @Builder.Default
    private double deletedRatio = 0.1;            // 删除数据比例
    
    @Builder.Default
    private String templateName = "default";       // 使用的模板名称
}

// 扩展后的生成请求
@Data
@Builder
public class TestDataGenerateRequestExt {
    private TestDataGenerateRequest basic;         // 基础请求参数
    private AdvancedGenerateOptions advanced;      // 高级选项
}

// 模板管理服务
@Service
@Slf4j
public class TestDataTemplateService {
    
    private final Map<String, TestDataTemplate> templates = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        // 初始化默认模板
        TestDataTemplate defaultTemplate = TestDataTemplate.builder()
            .templateName("default")
            .versionTemplate(createDefaultVersionTemplate())
            .testCaseTemplate(createDefaultTestCaseTemplate())
            .customFields(Arrays.asList("custom1", "custom2"))
            .enumValues(createDefaultEnumValues())
            .build();
            
        templates.put("default", defaultTemplate);
    }
    
    public void addTemplate(TestDataTemplate template) {
        templates.put(template.getTemplateName(), template);
    }
    
    public TestDataTemplate getTemplate(String templateName) {
        return templates.getOrDefault(templateName, templates.get("default"));
    }
    
    private Map<String, Object> createDefaultVersionTemplate() {
        Map<String, Object> template = new HashMap<>();
        template.put("status", Arrays.asList("ACTIVE", "INACTIVE", "DEPRECATED"));
        template.put("type", Arrays.asList("RELEASE", "HOTFIX", "FEATURE"));
        return template;
    }
    
    private Map<String, Object> createDefaultTestCaseTemplate() {
        Map<String, Object> template = new HashMap<>();
        template.put("priority", Arrays.asList("P0", "P1", "P2", "P3"));
        template.put("type", Arrays.asList("FUNCTIONAL", "PERFORMANCE", "SECURITY", "UI"));
        template.put("status", Arrays.asList("DRAFT", "READY", "OBSOLETE"));
        return template;
    }
    
    private Map<String, List<String>> createDefaultEnumValues() {
        Map<String, List<String>> enums = new HashMap<>();
        enums.put("platforms", Arrays.asList("Windows", "Linux", "MacOS"));
        enums.put("browsers", Arrays.asList("Chrome", "Firefox", "Safari"));
        return enums;
    }
}

// 关联关系生成器
@Component
@Slf4j
public class TestCaseRelationGenerator {
    
    private final Random random = new Random();
    
    public void generateRelations(List<TestCaseInfo> testCases, double density) {
        int totalCases = testCases.size();
        int targetRelations = (int)(totalCases * density);
        
        for (int i = 0; i < targetRelations; i++) {
            TestCaseInfo source = testCases.get(random.nextInt(totalCases));
            TestCaseInfo target = testCases.get(random.nextInt(totalCases));
            
            if (!source.equals(target)) {
                addRelation(source, target, randomRelationType());
            }
        }
    }
    
    private void addRelation(TestCaseInfo source, TestCaseInfo target, String type) {
        Map<String, Object> relation = new HashMap<>();
        relation.put("targetId", target.getId());
        relation.put("type", type);
        relation.put("createTime", new Date());
        
        if (source.getBusinessData().containsKey("relations")) {
            ((List<Map<String, Object>>) source.getBusinessData().get("relations")).add(relation);
        } else {
            List<Map<String, Object>> relations = new ArrayList<>();
            relations.add(relation);
            source.getBusinessData().put("relations", relations);
        }
    }
    
    private String randomRelationType() {
        String[] types = {"DEPENDS_ON", "BLOCKS", "RELATES_TO", "DUPLICATES"};
        return types[random.nextInt(types.length)];
    }
}

// 标签生成器
@Component
@Slf4j
public class TestCaseTagGenerator {
    
    private final Random random = new Random();
    
    private final List<String> commonTags = Arrays.asList(
        "smoke", "regression", "critical", "automated",
        "manual", "performance", "security", "ui",
        "api", "database", "integration", "unit"
    );
    
    public void generateTags(List<TestCaseInfo> testCases) {
        testCases.forEach(this::generateTagsForCase);
    }
    
    private void generateTagsForCase(TestCaseInfo testCase) {
        int tagCount = 1 + random.nextInt(3);  // 1-3个标签
        Set<String> selectedTags = new HashSet<>();
        
        while (selectedTags.size() < tagCount) {
            selectedTags.add(commonTags.get(random.nextInt(commonTags.size())));
        }
        
        testCase.getBusinessData().put("tags", new ArrayList<>(selectedTags));
    }
}

// 附件信息生成器
@Component
@Slf4j
public class TestCaseAttachmentGenerator {
    
    private final Random random = new Random();
    
    public void generateAttachments(List<TestCaseInfo> testCases) {
        testCases.forEach(this::generateAttachmentsForCase);
    }
    
    private void generateAttachmentsForCase(TestCaseInfo testCase) {
        if (random.nextDouble() < 0.3) {  // 30%的用例有附件
            int attachmentCount = 1 + random.nextInt(2);  // 1-2个附件
            List<Map<String, Object>> attachments = new ArrayList<>();
            
            for (int i = 0; i < attachmentCount; i++) {
                attachments.add(generateAttachment());
            }
            
            testCase.getBusinessData().put("attachments", attachments);
        }
    }
    
    private Map<String, Object> generateAttachment() {
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("id", UUID.randomUUID().toString());
        attachment.put("name", generateAttachmentName());
        attachment.put("type", randomAttachmentType());
        attachment.put("size", random.nextInt(10000000));  // 0-10MB
        attachment.put("createTime", new Date());
        return attachment;
    }
    
    private String generateAttachmentName() {
        String[] prefixes = {"screenshot_", "testdata_", "config_", "result_"};
        String[] extensions = {".png", ".jpg", ".pdf", ".xlsx"};
        
        return prefixes[random.nextInt(prefixes.length)] + 
               UUID.randomUUID().toString().substring(0, 8) +
               extensions[random.nextInt(extensions.length)];
    }
    
    private String randomAttachmentType() {
        String[] types = {"image/png", "image/jpeg", "application/pdf", "application/excel"};
        return types[random.nextInt(types.length)];
    }
}

// 增强的测试数据生成服务
@Service
@Slf4j
public class EnhancedTestDataGenerateService extends TestDataGenerateService {

    @Autowired
    private TestDataTemplateService templateService;
    
    @Autowired
    private TestCaseRelationGenerator relationGenerator;
    
    @Autowired
    private TestCaseTagGenerator tagGenerator;
    
    @Autowired
    private TestCaseAttachmentGenerator attachmentGenerator;
    
    public TestDataGenerateResponse generateEnhancedTestData(TestDataGenerateRequestExt request) {
        // 获取模板
        TestDataTemplate template = templateService.getTemplate(
            request.getAdvanced().getTemplateName());
            
        // 生成基础数据
        TestDataGenerateResponse response = super.generateTestData(request.getBasic());
        
        // 获取生成的测试用例
        List<TestCaseInfo> testCases = testCaseRepository.findByVersionId(
            request.getBasic().getVersionId());
            
        // 根据高级选项生成额外数据
        if (request.getAdvanced().isGenerateRelations()) {
            relationGenerator.generateRelations(testCases, 0.2); // 20%的关联密度
        }
        
        if (request.getAdvanced().isGenerateTags()) {
            tagGenerator.generateTags(testCases);
        }
        
        if (request.getAdvanced().isGenerateAttachments()) {
            attachmentGenerator.generateAttachments(testCases);
        }
        
        // 更新测试用例
        testCaseRepository.saveAll(testCases);
        
        return response;
    }
}

// 增强的控制器
@RestController
@RequestMapping("/api/test-data")
@Slf4j
public class EnhancedTestDataController extends TestDataController {

    @Autowired
    private EnhancedTestDataGenerateService enhancedService;
    
    @Autowired
    private TestDataTemplateService templateService;
    
    /**
     * 使用高级选项生成测试数据
     */
    @PostMapping("/generate/enhanced")
    public ResponseEntity<TestDataGenerateResponse> generateEnhancedTestData(
            @RequestBody @Valid TestDataGenerateRequestExt request) {
        log.info("Generating enhanced test data for request: {}", request);
        TestDataGenerateResponse response = enhancedService.generateEnhancedTestData(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 添加数据模板
     */
    @PostMapping("/template")
    public ResponseEntity<Void> addTemplate(@RequestBody @Valid TestDataTemplate template) {
        templateService.addTemplate(template);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 获取数据模板
     */
    @GetMapping("/template/{templateName}")
    public ResponseEntity<TestDataTemplate> getTemplate(@PathVariable String templateName) {
        TestDataTemplate template = templateService.getTemplate(templateName);
        return ResponseEntity.ok(template);
    }
}
