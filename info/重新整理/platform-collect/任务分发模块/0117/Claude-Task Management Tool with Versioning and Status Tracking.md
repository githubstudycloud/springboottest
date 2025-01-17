## Prompt:
新增一个任务表，记录类型和version，方法，这2个主键，提供新增，修改，查询的mybatis+xml方法并提供一个service，可以传入任何一个方法参数执行，同时记录变更状态，，如果下次传进来的方法参数一样，就判断状态是否执行完，没执行完就不执行，直接返回，给出这样的几个类文件，用于spring boot2.7.18，mysql是5.7，其他配置都有，实现这个工具主干，给出调用方法就行

## Response:
我将帮你实现一个任务表的核心功能，包括任务的记录、执行和状态管理。我会创建相关的实体类、mapper、service等组件。

Task Implementation Classes

Click to open code

这个实现包含以下主要组件:

1.  `TaskEntity`: 任务记录表的实体类,包含任务的基本信息和状态。
2.  `TaskMapper`: MyBatis mapper接口,提供数据库操作方法。
3.  `TaskParamsWrapper`: 任务参数的封装类。
4.  `TaskService`: 核心服务类,提供任务执行和状态管理功能:
    *   参数校验
    *   检查重复执行
    *   任务状态管理
    *   异常处理
5.  数据库表结构(task\_record)

主要功能:

*   支持任务执行前检查是否存在相同参数的执行中任务
*   记录任务执行状态和错误信息
*   支持重试计数
*   使用事务确保数据一致性

使用这个实现,你可以轻松地:

1.  记录方法执行状态
2.  防止重复执行
3.  追踪执行历史
4.  管理任务状态

注意事项:

1.  需要配置MySQL数据源和MyBatis
2.  需要在application.properties/yaml中配置相应的数据库连接信息
3.  需要创建task\_record表
4.  需要添加必要的依赖(mysql-connector-java, mybatis-spring-boot-starter等)

要使用这个实现,你需要:

1.  创建相应的类文件
2.  执行schema.sql创建表
3.  在Spring Boot应用中注入TaskService
4.  按示例代码调用executeTask方法

是否需要我详细解释某个部分的实现或提供更多的使用示例？

