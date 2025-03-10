// 1. Entity 基础类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity<T> {
    private Long id;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted; // 0-未删除,1-已删除
}

// 2. 通用Mapper接口
public interface GenericMapper<T extends BaseEntity> {
    List<T> selectByCondition(Map<String, Object> params);
    int batchInsert(List<T> entities);
    int batchUpdate(List<T> entities);
    int softDelete(List<Long> ids);
    int hardDelete(List<Long> ids);
}

// 3. XML配置示例 (保存为GenericMapper.xml)
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.mapper.GenericMapper">
    <!-- 动态条件查询 -->
    <select id="selectByCondition" resultType="BaseEntity">
        SELECT * FROM ${tableName} 
        <where>
            <if test="id != null">AND id = #{id}</if>
            <if test="isDeleted != null">AND is_deleted = #{isDeleted}</if>
            <!-- 其他条件可根据实际Bean属性添加 -->
        </where>
    </select>

    <!-- 批量插入 -->
    <insert id="batchInsert" parameterType="java.util.List">
        INSERT INTO ${tableName} 
        (id, create_time, update_time, is_deleted, <!-- 其他字段 -->)
        VALUES
        <foreach collection="list" item="item" separator=",">
            (#{item.id}, NOW(), NOW(), 0, <!-- 其他属性值 -->)
        </foreach>
    </insert>

    <!-- 批量更新 -->
    <update id="batchUpdate" parameterType="java.util.List">
        <foreach collection="list" item="item" separator=";">
            UPDATE ${tableName}
            SET update_time = NOW()
            <!-- 其他需要更新的字段 -->
            WHERE id = #{item.id} AND is_deleted = 0
        </foreach>
    </update>

    <!-- 软删除 -->
    <update id="softDelete" parameterType="java.util.List">
        UPDATE ${tableName}
        SET is_deleted = 1, update_time = NOW()
        WHERE id IN
        <foreach collection="list" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </update>

    <!-- 硬删除 -->
    <delete id="hardDelete" parameterType="java.util.List">
        DELETE FROM ${tableName}
        WHERE id IN
        <foreach collection="list" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </delete>
</mapper>

// 4. Service接口
public interface GenericService<T extends BaseEntity> {
    List<T> queryByCondition(Map<String, Object> params);
    BatchResult processBatch(List<T> entities, OperationType opType);
}

// 5. 操作类型枚举
public enum OperationType {
    INSERT, UPDATE, DELETE_SOFT, DELETE_HARD
}

// 6. 批处理结果类
@Data
@AllArgsConstructor
public class BatchResult {
    private boolean success;
    private int affectedRows;
    private String message;
    private List<String> errors;
}

// 7. Service实现类
@Service
@Slf4j
public class GenericServiceImpl<T extends BaseEntity> implements GenericService<T> {
    
    @Autowired
    private GenericMapper<T> mapper;
    
    @Value("${batch.size:500}")
    private int batchSize;
    
    @Override
    public List<T> queryByCondition(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        // 默认查询未删除记录
        if (!params.containsKey("isDeleted")) {
            params.put("isDeleted", 0);
        }
        return mapper.selectByCondition(params);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchResult processBatch(List<T> entities, OperationType opType) {
        // 参数验证
        if (CollectionUtils.isEmpty(entities)) {
            return new BatchResult(false, 0, "参数列表为空", null);
        }
        
        List<String> errors = validateEntities(entities, opType);
        if (!CollectionUtils.isEmpty(errors)) {
            return new BatchResult(false, 0, "参数验证失败", errors);
        }
        
        int totalAffected = 0;
        try {
            // 分批处理
            List<List<T>> batches = ListUtils.partition(entities, batchSize);
            for (List<T> batch : batches) {
                int affected = processOneBatch(batch, opType);
                totalAffected += affected;
            }
            return new BatchResult(true, totalAffected, "操作成功", null);
        } catch (Exception e) {
            log.error("批处理操作失败", e);
            throw new RuntimeException("批处理操作异常: " + e.getMessage());
        }
    }
    
    private int processOneBatch(List<T> batch, OperationType opType) {
        switch (opType) {
            case INSERT:
                return mapper.batchInsert(batch);
            case UPDATE:
                return mapper.batchUpdate(batch);
            case DELETE_SOFT:
                return mapper.softDelete(batch.stream()
                    .map(BaseEntity::getId)
                    .collect(Collectors.toList()));
            case DELETE_HARD:
                return mapper.hardDelete(batch.stream()
                    .map(BaseEntity::getId)
                    .collect(Collectors.toList()));
            default:
                throw new IllegalArgumentException("不支持的操作类型");
        }
    }
    
    private List<String> validateEntities(List<T> entities, OperationType opType) {
        List<String> errors = new ArrayList<>();
        
        for (int i = 0; i < entities.size(); i++) {
            T entity = entities.get(i);
            String prefix = "第" + (i + 1) + "条记录: ";
            
            // 基础验证
            if (entity == null) {
                errors.add(prefix + "实体为空");
                continue;
            }
            
            // 操作类型特定验证
            switch (opType) {
                case INSERT:
                    // 插入时ID应为空或特定值
                    if (entity.getId() != null && entity.getId() > 0) {
                        errors.add(prefix + "新增记录不应指定ID");
                    }
                    // 其他新增特定验证...
                    break;
                    
                case UPDATE:
                case DELETE_SOFT:
                case DELETE_HARD:
                    // 更新和删除必须有ID
                    if (entity.getId() == null || entity.getId() <= 0) {
                        errors.add(prefix + "ID不能为空");
                    }
                    break;
            }
            
            // 通用业务规则验证（根据实际情况添加）
            validateBusinessRules(entity, errors, prefix);
        }
        
        return errors;
    }
    
    // 具体业务规则验证（需具体实现类重写）
    protected void validateBusinessRules(T entity, List<String> errors, String prefix) {
        // 默认实现为空，由子类根据特定业务规则重写
    }
}

// 8. Controller示例
@RestController
@RequestMapping("/api/items")
public class ItemController {
    
    @Autowired
    private GenericService<ItemEntity> itemService;
    
    @GetMapping
    public ResponseEntity<List<ItemEntity>> query(
            @RequestParam(required = false) Map<String, Object> params) {
        return ResponseEntity.ok(itemService.queryByCondition(params));
    }
    
    @PostMapping("/batch")
    public ResponseEntity<BatchResult> processBatch(
            @RequestBody List<ItemEntity> items,
            @RequestParam OperationType opType) {
        
        // 参数校验
        if (items == null || items.isEmpty()) {
            return ResponseEntity.badRequest().body(
                new BatchResult(false, 0, "请求参数为空", null));
        }
        
        if (opType == null) {
            return ResponseEntity.badRequest().body(
                new BatchResult(false, 0, "操作类型不能为空", null));
        }
        
        // 调用服务
        BatchResult result = itemService.processBatch(items, opType);
        
        // 根据结果返回
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}
