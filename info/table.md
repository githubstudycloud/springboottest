我将帮你创建一个检查并追加表字段的功能。我们需要创建相关的实体类、Mapper和Service来实现这个功能。



```java
package com.study.scheduler.util;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 数据库字段管理器 - 用于检查和添加表字段
 */
@Service
public class DatabaseFieldManager {
    
    @Autowired
    private DatabaseFieldMapper databaseFieldMapper;
    
    /**
     * 检查并添加字段
     * @param tableName 表名
     * @param fieldName 字段名
     * @param fieldDefinition 字段定义(如: VARCHAR(100) NOT NULL COMMENT '注释')
     * @return 是否进行了修改
     */
    @Transactional
    public boolean checkAndAddField(String tableName, String fieldName, String fieldDefinition) {
        try {
            // 检查字段是否存在
            List<Map<String, Object>> columns = databaseFieldMapper.checkFieldExists(tableName, fieldName);
            
            if (columns.isEmpty()) {
                // 字段不存在，添加字段
                String alterSql = String.format("ALTER TABLE %s ADD COLUMN %s %s", 
                    tableName, fieldName, fieldDefinition);
                databaseFieldMapper.executeAlterTable(alterSql);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("检查或添加字段失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查表是否存在
     * @param tableName 表名
     * @return 是否存在
     */
    public boolean checkTableExists(String tableName) {
        try {
            return !databaseFieldMapper.checkTableExists(tableName).isEmpty();
        } catch (Exception e) {
            throw new RuntimeException("检查表是否存在失败: " + e.getMessage(), e);
        }
    }
}

@Mapper
interface DatabaseFieldMapper {
    
    /**
     * 检查字段是否存在
     */
    @Select("""
        SELECT COLUMN_NAME
        FROM INFORMATION_SCHEMA.COLUMNS 
        WHERE TABLE_SCHEMA = (SELECT DATABASE()) 
        AND TABLE_NAME = #{tableName} 
        AND COLUMN_NAME = #{fieldName}
    """)
    List<Map<String, Object>> checkFieldExists(String tableName, String fieldName);
    
    /**
     * 检查表是否存在
     */
    @Select("""
        SELECT TABLE_NAME
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_SCHEMA = (SELECT DATABASE())
        AND TABLE_NAME = #{tableName}
    """)
    List<Map<String, Object>> checkTableExists(String tableName);
    
    /**
     * 执行ALTER TABLE语句
     */
    @Update("${sql}")
    void executeAlterTable(String sql);
}

```

使用示例：

```java
@Autowired
private DatabaseFieldManager databaseFieldManager;

public void checkAndAddFields() {
    // 检查表是否存在
    if (databaseFieldManager.checkTableExists("your_table")) {
        // 检查并添加字段
        databaseFieldManager.checkAndAddField(
            "your_table",
            "new_field",
            "VARCHAR(100) NOT NULL DEFAULT '' COMMENT '新字段'"
        );
    }
}
```

主要功能：

1. 支持检查表是否存在
2. 支持检查字段是否存在
3. 支持自动添加新字段
4. 使用事务确保操作的原子性
5. 提供详细的错误信息

注意事项：

1. 在使用前确保数据库用户有ALTER TABLE权限
2. fieldDefinition参数需要符合MySQL语法
3. 建议在正式环境使用前先在测试环境验证
4. 所有SQL操作都在事务中执行，确保数据一致性

使用该工具类可以安全地检查和添加数据库字段，避免了手动执行SQL的风险。如果你需要添加其他功能（如修改字段定义、删除字段等），我可以为你扩展这个工具类。