## Prompt:
这个实体使用对象池之后没有生成urihash，而且保存到mongodb只有1条数据，请分析原因并修改

## Response:
分析了代码后，发现问题主要出在以下几个方面：

1.  UriEntity 对象池重用时的问题：

*   在 ObjectPoolConfig 中的 passivateObject 方法调用了 entity.reset()，这会清空所有字段包括 uriHash
*   但在重用对象时没有正确重新生成 uriHash

2.  convertEntityToDocument 时的问题：

*   UriRepository 的 convertEntityToDocument 方法中没有检查 uriHash 是否为空
*   如果 uriHash 为空就会导致文档 ID 重复，从而只能保存一条数据

让我们来修改这些问题：

1.  首先修改 UriEntity 的 reset 方法：

```java
public void reset() {
    super.reset(); // 调用父类的 reset
    this.uri = null;
    this.uriHash = null;  // 继续清空 uriHash
    this.rootNode = null;
    this.versionType = null;
    this.uriVersion = null;
    this.details = null;
    this.deleted = false;
    this.version = 0L;
    this.versionCode = null;
    this.versionTime = null;
}
```

2.  修改 CollectExecutor 中的 fillEntity 方法，确保设置完 uri 后生成 uriHash：

```java
private void fillEntity(
        UriEntity entity,
        String rootNode,
        String version,
        Map<String, Object> detail
) {
    String uri = (String) detail.get("uri");
    entity.setUri(uri);
    entity.setUriHash(HashUtil.hash(uri));  // 重要：设置完 uri 后立即生成 uriHash
    entity.setRootNode(rootNode);
    entity.setVersionType(getVersionType(version));
    entity.setUriVersion(version);
    entity.setDetails(detail);
}
```

3.  修改 UriRepository 的 convertEntityToDocument 方法，增加 uriHash 的检查和生成：

```java
private Document convertEntityToDocument(UriEntity entity) {
    // 确保 uriHash 存在
    if (entity.getUriHash() == null && entity.getUri() != null) {
        entity.setUriHash(HashUtil.hash(entity.getUri()));
    }
    
    Document doc = new Document();
    doc.put("uri", entity.getUri());
    doc.put("uri_hash", entity.getUriHash());
    doc.put("root_node", entity.getRootNode());
    doc.put("version_type", entity.getVersionType());
    doc.put("uri_version", entity.getUriVersion());
    doc.put("details", entity.getDetails());
    doc.put("version", entity.getVersion());
    doc.put("version_code", entity.getVersionCode());
    doc.put("version_time", entity.getVersionTime());
    doc.put("update_time", LocalDateTime.now());
    doc.put("is_deleted", false);

    if (entity.getCreateTime() == null) {
        doc.put("create_time", LocalDateTime.now());
    }

    return doc;
}
```

4.  在 ObjectPoolConfig 中增加对象验证：

```java
@Override
public boolean validateObject(PooledObject<UriEntity> p) {
    UriEntity entity = p.getObject();
    // 确保对象有效且关键字段正确
    return entity != null && 
           (entity.getUri() == null || // 如果 uri 为空说明是新对象
            (entity.getUriHash() != null && entity.getUriHash().equals(HashUtil.hash(entity.getUri())))); // 如果有 uri 则验证 hash
}
```

5.  在 UriRepository 的 batchUpsert 方法中添加验证：

```java
public BulkWriteResult batchUpsert(String rootNode, List<UriEntity> entities) {
    if (CollectionUtils.isEmpty(entities)) {
        return null;
    }

    // 验证所有实体的 uriHash
    entities.forEach(entity -> {
        if (entity.getUriHash() == null && entity.getUri() != null) {
            entity.setUriHash(HashUtil.hash(entity.getUri()));
        }
    });

    Timer.Sample timer = Timer.start(meterRegistry);
    String collectionName = TableNameHelper.getTableName(rootNode);
    MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
    
    // 其余代码保持不变
    ...
}
```

这些修改确保了：

1.  每个 URI 实体都有正确的 uriHash
2.  对象池中的对象在重用时会正确重置并生成新的 uriHash
3.  保存到 MongoDB 时会验证并确保 uriHash 存在
4.  避免了文档 ID 重复的问题

通过这些修改，应该能解决只保存一条数据的问题，并确保每个文档都有正确的 uriHash。

