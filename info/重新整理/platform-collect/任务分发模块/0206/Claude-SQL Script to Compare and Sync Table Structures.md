## Prompt:
使用sql比较2个表的结构，根据第一个表输出第二个表改结构变成第一个表的变更语句

## Response:
我帮你写一个SQL脚本来比较两个表结构并生成变更语句。

表结构比较和变更SQL生成器

Click to open code

使用方法:

1.  将@table1\_name替换为基准表名
2.  将@table2\_name替换为需要修改的表名
3.  将@schema\_name替换为数据库schema名称

脚本会生成三类SQL语句:

*   添加缺失的列
*   修改不匹配的列定义
*   删除多余的列

## Prompt:
我是mysql5.7

## Response:
好的，让我调整一下脚本以适配MySQL 5.7。

表结构比较和变更SQL生成器

Click to open code

使用方法:

1.  将脚本中的'table1'替换为基准表名
2.  将'table2'替换为需要修改的表名

这个版本:

*   使用MySQL 5.7特有的COLUMN\_TYPE替代了之前的数据类型组合
*   正确处理了默认值的引号问题
*   支持AUTO\_INCREMENT等EXTRA属性
*   按DROP、MODIFY、ADD顺序排序以避免依赖问题

## Prompt:
把表名设置为变量，方便快速生成

## Response:
表结构比较和变更SQL生成器

Click to open code

使用方法：修改开头的@table1和@table2变量值即可。

## Prompt:
检索数据库中特定前缀的表是否和基准表结构一致，并输出修改语句，这个单独写一个，第二个也是一个，封装成2个存储过程

## Response:
Untitled

Click to open code

使用方法:

```sql
-- 检查前缀表结构
CALL compare_prefix_tables('base_table', 'prefix_');

-- 生成修改语句
CALL generate_alter_statements('base_table', 'target_table');
```

