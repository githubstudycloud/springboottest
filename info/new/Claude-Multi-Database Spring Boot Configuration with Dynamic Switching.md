## Prompt:
对于springboot3.2.9追加3个mysql数据源，其中2个固定的单数据源，一个公共库，一个采集库，剩下一个业务库，能追加切换10个及以上的数据库，通过配置别名，传入别名可切换，配置项都写在一个额外的本地配置文件里，每个数据库配置别名，url，账户名和密码，同时业务库的所有数据库都有相同前缀，方便自动检索业务库加载给出切换工具类，并给出3个dao文件夹和xml文件夹，用mybatis，请给出完整示例代码模板并附带测试表和测试接口验证服务

## Response:
I'll create a complete Spring Boot 3.2.9 configuration with three MySQL data sources - two fixed sources (public and collection databases) and one dynamic business database that can switch between 10+ databases based on an alias. Let me provide a full implementation with test interfaces to verify the configuration.

SpringBoot 3.2.9 Multi-Datasource Configuration

Click to open code

Tap to open

## Prompt:
继续

## Response:
SpringBoot 3.2.9 Multi-Datasource Configuration (Continued)

Click to open code

Tap to open

这个完整的Spring Boot 3.2.9多数据源配置示例满足您的所有要求：

1.  **三种数据源类型**：
    *   公共库（固定）
    *   采集库（固定）
    *   业务库（动态可切换10+数据库）
2.  **别名配置与切换**：
    *   所有配置都集中在独立的`datasource-config.yml`配置文件中
    *   业务库有统一前缀`biz_`，便于自动检索
    *   通过别名可以随时切换业务数据源
3.  **核心配置组件**：
    *   `DynamicDataSource`：继承自`AbstractRoutingDataSource`实现数据源动态切换
    *   `DataSourceSwitchUtil`：提供切换数据源的工具方法
    *   三组独立的数据访问层（DAO/XML）
4.  **项目结构特点**：
    *   标准三层架构（Controller/Service/DAO）
    *   数据源隔离，每个数据源有独立的Mapper扫描路径
    *   代码完全按照生产环境标准编写
5.  **测试组件**：
    *   包含了测试表的SQL脚本
    *   实现了完整的REST API接口用于验证
    *   预置测试数据便于快速验证功能

使用方式：

1.  将所有文件按项目结构组织
2.  运行SQL脚本创建测试数据库和表
3.  启动应用程序
4.  使用如下测试接口验证：

```
# 获取所有可用数据源
GET /api/datasources

# 获取默认数据源的用户
GET /api/users

# 获取指定数据源的用户
GET /api/users/{dbAlias}  # 例如 /api/users/db2

# 向指定数据源添加用户
POST /api/users/{dbAlias}
请求体: {"username":"testUser","email":"test@example.com"}
```

您可以轻松地通过传入不同的别名来动态切换业务数据库，而公共库和采集库保持稳定。

