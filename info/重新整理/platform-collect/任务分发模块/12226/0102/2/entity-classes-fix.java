// UriEntity.java
@Document(collection = "uri_collect")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor  // 添加无参构造器
@AllArgsConstructor
public class UriEntity extends VersionEntity {
    @Indexed(unique = true)
    private String uriHash;
    private String uri;
    private String rootNode;
    private String versionType;
    private String uriVersion;
    private Map<String, Object> details;
}

// VersionEntity.java
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor  // 添加无参构造器
public abstract class VersionEntity extends BaseEntity {
    protected String versionCode;
    protected LocalDateTime versionTime;

    public void initVersion() {
        this.version = 0L;
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    public void upgradeVersion() {
        this.version = this.version + 1;
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    protected String generateVersionCode() {
        return String.format("V%s_%d",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                this.version);
    }
}

// BaseEntity.java
@Data
@NoArgsConstructor  // 添加无参构造器
public abstract class BaseEntity implements Serializable {
    @Id
    protected String id;

    @CreatedDate
    protected LocalDateTime createTime;

    @LastModifiedDate
    protected LocalDateTime updateTime;

    @CreatedBy
    protected String createBy;

    @LastModifiedBy
    protected String updateBy;

    @Version
    protected Long version;

    protected Boolean deleted = false;
}

// ObjectPoolConfig.java 的主要部分修改
@Bean
public GenericObjectPool<UriEntity> uriEntityPool() {
    GenericObjectPoolConfig<UriEntity> poolConfig = new GenericObjectPoolConfig<>();
    poolConfig.setMaxTotal(20);
    poolConfig.setMaxIdle(10);
    poolConfig.setMinIdle(5);
    
    return new GenericObjectPool<>(new BasePooledObjectFactory<>() {
        @Override
        public UriEntity create() {
            log.debug("Creating new UriEntity in pool");
            UriEntity entity = new UriEntity();  // 这里应该能正常创建了
            entity.setVersion(0L);
            entity.setDeleted(false);
            log.debug("Created new UriEntity in pool");
            return entity;
        }

        @Override
        public PooledObject<UriEntity> wrap(UriEntity entity) {
            return new DefaultPooledObject<>(entity);
        }

        @Override
        public void passivateObject(PooledObject<UriEntity> p) {
            UriEntity entity = p.getObject();
            // 重置基本字段
            entity.setUri(null);
            entity.setUriHash(null);
            entity.setRootNode(null);
            entity.setVersionType(null);
            entity.setUriVersion(null);
            entity.setDetails(null);
            // 重置继承的字段
            entity.setId(null);
            entity.setCreateTime(null);
            entity.setUpdateTime(null);
            entity.setCreateBy(null);
            entity.setUpdateBy(null);
            entity.setVersion(0L);
            entity.setDeleted(false);
            entity.setVersionCode(null);
            entity.setVersionTime(null);
        }
    }, poolConfig);
}