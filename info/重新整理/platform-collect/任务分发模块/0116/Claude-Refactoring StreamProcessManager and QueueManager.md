## Prompt:
我的新设计见文档，我的新代码见project\_overview文件，但现在这个StreamProcessManager并没有能发挥作用，而且QueueManager的public QueueManager(ThreadPoolTaskExecutor taskExecutor) {无法自动装配。存在多个 'ThreadPoolTaskExecutor' 类型的 Bean。Beans:applicationTaskExecutor (TaskExecutorConfigurations.class)taskExecutor (ThreadPoolConfig.java)，还有删除时直接uri有20多M，会报错，也没有利用UriCleanupService的方式来删除，UriCleanupService也没有被StreamProcessManager管理，没能很好的实现这些“UriCleanupService整和到StreamProcessManager你能控制dao方法的动态传入吗？还有crontroller采集接口异步返回成功，进入到采集时做好单个采集请求和多个采集请求的线程管理，http访问第三方接口，第三方接口的请求总数要限制在1分钟500个并发以内，mongodb的整个插入也要限制在线程池管理的范围内，尤其有多个采集接口请求进来时，要做好排队管理，同时能查询这个排队和把某个请求提前，UriCleanupService也是小版本删除也要做好并发控制和队列以及批量，加快删除速度，单个请求可控，多个请求也可控，表名动态新增修改根据UriEntity的rootNode，删除根据List<uri>，rootNode,查询根据List<uri>动态找表名去表名前缀匹配uri再查询，如果查询传入rootNode，那就直接用，要有软删除和硬删除方法可选，查询也要能动态选择这个删除标记。还有修改之前代码里一些冗余，错误，不合理的部分，现在再次输出一下你准备实现的目录结构，和流程设计说明，最后确认后，我们开始完整分段输出代码，代码不要省略，包括import和package” 请检查代码，给出需要修改新增的文件和目录结构，还有现在我aspect下面的切换表名的代码应该没用了吧，确认好这些我们再分段输出完整代码

## Response:
我来分析下现有的问题和需要改进的地方：

1.  StreamProcessManager 相关问题:

*   需要改造成更通用的处理管理器
*   支持动态传入 DAO 方法
*   需要整合 UriCleanupService
*   支持批量处理和队列管理

2.  QueueManager 依赖注入问题:

*   需要明确指定注入的 ThreadPoolTaskExecutor
*   修改构造函数,支持多个线程池配置

3.  大容量删除问题:

*   利用 StreamProcessManager 管理删除流程
*   实现分批处理机制
*   支持软删除和硬删除
*   优化表名动态匹配

4.  表名动态管理:

*   移除原有的 aspect 切面代码
*   改用 repository 层动态处理表名
*   优化表名前缀匹配逻辑

5.  并发控制:

*   HTTP 请求限流(500/min)
*   MongoDB 操作限流
*   队列优先级管理
*   任务取消/暂停支持

需要新增/修改的文件结构:

```
src/main/java/com/study/collect/business/testcase/
    common/
        constants/              # 常量定义
        enums/                 # 枚举定义  
        utils/                 # 工具类
            StreamProcessor.java  # 通用流式处理器
            TableNameHelper.java  # 表名处理工具
    config/
        ThreadPoolConfig.java     # 线程池配置(修改)
    core/                     # 核心处理层
        executor/            # 执行器
            CollectExecutor.java
            DeleteExecutor.java
        manager/             # 管理器
            QueueManager.java      # 队列管理(修改) 
            TaskManager.java       # 任务管理
        processor/           # 处理器
            DataProcessor.java     # 数据处理接口
            CollectProcessor.java  # 采集处理器
            DeleteProcessor.java   # 删除处理器
    service/
        impl/
            UriCollectServiceImpl.java  # 实现类(修改)
```

主要改进点:

1.  移除 aspect 相关代码,改用工具类处理表名
2.  重构 StreamProcessor:

*   支持泛型和函数式接口
*   增加处理器链
*   支持暂停/恢复

3.  优化线程池配置:

*   明确区分不同用途线程池
*   支持动态调整参数

4.  完善队列管理:

*   支持优先级调度
*   任务状态跟踪
*   限流控制

您觉得这个方案如何?后续我们可以逐个文件详细完善实现。

请确认改造方案,我们再进行具体的代码实现。

## Prompt:
我确认这样改造，请按顺序逐个文件详细完善实现

## Response:
好的,我们开始逐个文件详细实现。先从基础的常量和工具类开始。

第一部分,我们先实现通用工具类和常量:

1.  常量定义:

CollectionConstants.java

Click to open code

2.  表名处理工具类:

TableNameHelper.java

Click to open code

3.  流式处理器基础类:

StreamProcessor.java

Click to open code

## Prompt:
继续

## Response:
继续完成StreamProcessor.java的剩余部分：

StreamProcessor.java

Click to open code

接下来创建处理器接口：

DataProcessor.java

Click to open code

这些是基础设施类的实现。主要包括：

1.  CollectionConstants：整合所有常量配置，采用内部类分类管理
2.  TableNameHelper：替代原有切面的表名处理工具
3.  StreamProcessor：通用流式处理框架，支持：
    *   批量处理
    *   重试机制
    *   进度跟踪
    *   异步处理
    *   暂停/恢复
4.  DataProcessor：处理器接口定义

接下来我们需要实现：

1.  ThreadPoolConfig - 线程池配置
2.  核心执行器(CollectExecutor/DeleteExecutor)
3.  QueueManager和TaskManager
4.  具体的处理器实现类

您想先看哪部分的实现？

## Prompt:
按顺序继续

## Response:
好的,我们继续按顺序实现,先完成线程池配置：

ThreadPoolConfig.java

Click to open code

接下来实现采集执行器:

CollectExecutor.java

Click to open code

这部分已经实现了:

1.  ThreadPoolConfig - 明确区分四种线程池:
    *   HTTP请求线程池
    *   MongoDB操作线程池
    *   任务处理线程池
    *   调度线程池
    *   虚拟线程池(可选)
2.  CollectExecutor - 采集执行器:
    *   使用StreamProcessor管理处理流程
    *   集成HTTP限流
    *   实现对象池管理
    *   批量处理优化

接下来需要实现:

1.  DeleteExecutor - 删除执行器
2.  QueueManager - 队列管理
3.  TaskManager - 任务管理

要继续实现删除执行器吗？

## Prompt:
按顺序继续

## Response:
好的，继续实现删除执行器：

DeleteExecutor.java

Click to open code

接下来实现队列管理器：

QueueManager.java

Click to open code

## Prompt:
继续

## Response:
继续完成QueueManager.java的剩余部分：

QueueManager.java

Click to open code

接下来实现任务管理器：

TaskManager.java

Click to open code

已完成以下组件的实现：

1.  QueueManager - 队列管理器:
    *   支持优先级队列
    *   任务状态追踪
    *   进度更新
    *   监控和清理功能
2.  TaskManager - 任务管理器:
    *   完整任务生命周期管理
    *   超时处理
    *   定期清理
    *   统计功能

这些组件共同构成了任务处理的核心框架。接下来我们需要:

1.  修改和优化UriRepository
2.  更新UriCollectService实现
3.  完善UriCleanupService

您想先看哪个部分的实现？

## Prompt:
QueueManager单独完整输出下

## Response:
好的，我来完整输出QueueManager的实现：

QueueManager.java

Click to open code

这是完整的QueueManager实现，主要特性包括：

1.  任务管理
    *   优先级队列实现
    *   任务生命周期管理
    *   支持任务取消和优先级调整
2.  并发处理
    *   多线程队列处理
    *   线程安全的状态管理
    *   异步任务执行
3.  错误处理
    *   自动重试机制
    *   错误状态追踪
    *   异常恢复
4.  监控和维护
    *   队列健康监控
    *   定期清理过期任务
    *   详细的统计信息
5.  扩展功能
    *   任务属性管理
    *   进度跟踪
    *   暂停/恢复支持

这个实现解决了原有的依赖注

## Prompt:
继续 修改和优化UriRepository \* 更新UriCollectService实现 \* 完善UriCleanupService

## Response:
好的，让我们按顺序继续实现。首先是优化 UriRepository：

UriRepository.java

Click to open code

接下来实现 UriCleanupService:

UriCleanupService.java

Click to open code

## Prompt:
UriCleanupService单独完整输出下

## Response:
好的，我来完整输出 UriCleanupService：

UriCleanupService.java

Click to open code

这是完整的 UriCleanupService 实现，主要特性包括：

1.  核心功能
    *   支持批量清理操作
    *   支持批量删除操作
    *   软删除和硬删除支持
    *   使用 StreamProcessor 进行流式处理
2.  参数配置
    *   CleanupParams - 清理参数配置
    *   DeleteParams - 删除参数配置
    *   支持批量大小配置
    *   支持超时配置
3.  错误处理
    *   参数验证
    *   异常处理和日志记录
    *   失败重试机制
4.  性能优化
    *   批量处理
    *   异步执行
    *   可配置的并发级别
5.  监控和统计
    *   进度跟踪
    *   结果统计
    *   详细的操作指标

现在要继续实现 UriCollectService 吗？

## Prompt:
继续

## Response:
好的，让我们实现完整的UriCollectService：

UriCollectServiceImpl.java

Click to open code

这是完整的 UriCollectService 实现，主要特性包括：

1.  核心功能
    *   异步数据采集
    *   批量删除处理
    *   增量同步支持
    *   查询功能
2.  任务管理
    *   任务创建和跟踪
    *   优先级管理
    *   进度更新
    *   状态监控
3.  队列管理
    *   任务排队
    *   优先级调度
    *   取消功能
4.  错误处理
    *   异常管理
    *   任务重试
    *   错误状态追踪
5.  整合功能
    *   集成清理服务
    *   集成执行器
    *   集成HTTP服务

这个实现解决了原有代码的以下问题：

1.  采集和删除逻辑分离
2.  队列管理更完善
3.  批量处理优化
4.  异常处理改进
5.  进度跟踪更准确

需要继续实现其他部分吗？

