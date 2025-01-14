## Prompt:
用monogodb查询以uri\_开头的所有表的deleted的字段的值为false的数据

## Response:
我来帮你写一个使用MongoDB查询以"uri\_"开头的所有集合中deleted字段为false的数据的SpringBoot应用。

SpringBoot MongoDB Query Example

Click to open code

这个实现提供了以下功能：

1.  使用MongoTemplate来操作MongoDB
2.  自动查找所有以"uri\_"开头的集合
3.  对每个匹配的集合执行查询，筛选deleted=false的文档
4.  返回查询结果的REST API接口

使用方法：

1.  配置好MongoDB连接信息
2.  启动应用后访问 `/api/query-uri-collections` 端点

如果你需要添加更多查询条件或修改返回格式，我可以帮你调整代码。

你也可以使用MongoDB命令行直接查询：

```javascript
// 获取所有集合名称
db.getCollectionNames().forEach(function(collectionName) {
    if (collectionName.startsWith('uri_')) {
        // 对每个匹配的集合执行查询
        db.getCollection(collectionName).find({deleted: false}).forEach(printjson);
    }
});
```

需要我帮你调整或优化哪些部分吗？

## Prompt:
MongoDB命令行同时直接查询并获取这些表的和

## Response:
我明白你想要汇总所有以"uri\_"开头的集合中的数据。我来帮你写一个MongoDB聚合查询。

```javascript
// 初始化结果计数器
let totalCount = 0;
let collectionResults = [];

// 查询并汇总所有以uri_开头的集合
db.getCollectionNames().forEach(function(collectionName) {
    if (collectionName.startsWith('uri_')) {
        // 获取当前集合中 deleted: false 的文档数量
        let count = db.getCollection(collectionName).count({deleted: false});
        totalCount += count;
        
        // 保存每个集合的结果
        collectionResults.push({
            collection: collectionName,
            count: count
        });
        
        // 打印每个集合的统计
        print(`集合 ${collectionName}: ${count} 条记录`);
    }
});

// 打印总计
print("\n总计统计:");
print(`总记录数: ${totalCount}`);
print("\n各集合明细:");
printjson(collectionResults);

// 如果你想同时查看具体数据，可以添加以下代码
print("\n详细数据:");
db.getCollectionNames().forEach(function(collectionName) {
    if (collectionName.startsWith('uri_')) {
        print(`\n${collectionName} 集合的数据:`);
        db.getCollection(collectionName)
          .find({deleted: false})
          .forEach(printjson);
    }
});
```

这个脚本会：

1.  计算每个以"uri\_"开头的集合中 deleted: false 的文档数量
2.  汇总所有符合条件的文档总数
3.  显示每个集合的统计信息
4.  可选地显示所有符合条件的具体文档内容

如果你只想看统计数据而不需要具体内容，可以去掉最后一部分代码。如果你需要添加其他统计维度或者修改查询条件，我也可以帮你调整。

