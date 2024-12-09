我来帮你从日志增强部分开始逐步实现。先从日志系统的基础架构开始：

1. 首先创建基础的日志实体和表结构：



```java
// 1. 机器日志实体
package com.study.collect.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "machine_logs")
public class CollectionMachineLog {
    @Id
    private String id;
    private String machineId;          // 机器ID
    private String machineIp;          // 机器IP
    private String machineName;        // 机器名称
    private Double cpuUsage;           // CPU使用率
    private Double memoryUsage;        // 内存使用率
    private Double diskUsage;          // 磁盘使用率
    private Date lastHeartbeatTime;    // 最后心跳时间
    private Integer taskCount;         // 当前任务数
    private Date createTime;           // 创建时间
    private Date updateTime;           // 更新时间
}

// 2. 采集任务日志实体
@Data
@Document(collection = "task_logs")
public class CollectionTaskLog {
    @Id
    private String id;
    private String taskId;             // 任务ID
    private String collectorId;        // 采集器ID
    private String machineId;          // 执行机器ID
    private Date startTime;            // 开始时间
    private Date endTime;              // 结束时间
    private Integer totalCount;        // 总数据量
    private Integer successCount;      // 成功数量
    private Integer failCount;         // 失败数量
    private String errorMessage;       // 错误信息
    private Map<String,Object> extras; // 扩展信息
    private Date createTime;           // 创建时间
    private Date updateTime;           // 更新时间
}

// 3. 采集链路日志实体
@Data
@Document(collection = "trace_logs")
public class CollectionTraceLog {
    @Id
    private String id;
    private String taskId;             // 任务ID
    private String traceId;            // 链路ID
    private String stage;              // 采集阶段
    private Date timestamp;            // 时间戳
    private String requestInfo;        // 请求信息
    private String responseInfo;       // 响应信息
    private Long costTime;             // 耗时(ms)
    private String errorStack;         // 异常堆栈
    private Map<String,Object> metrics;// 性能指标
}

// 4. 日志仓储接口
package com.study.collect.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Date;
import java.util.List;

public interface CollectionMachineLogRepository extends MongoRepository<CollectionMachineLog, String> {
    List<CollectionMachineLog> findByLastHeartbeatTimeAfter(Date time);
    List<CollectionMachineLog> findByMachineIdAndCreateTimeBetween(String machineId, Date startTime, Date endTime);
}

public interface CollectionTaskLogRepository extends MongoRepository<CollectionTaskLog, String> {
    List<CollectionTaskLog> findByTaskId(String taskId);
    List<CollectionTaskLog> findByCollectorIdAndStartTimeBetween(String collectorId, Date startTime, Date endTime);
}

public interface CollectionTraceLogRepository extends MongoRepository<CollectionTraceLog, String> {
    List<CollectionTraceLog> findByTaskIdOrderByTimestampAsc(String taskId);
    List<CollectionTraceLog> findByTraceId(String traceId);
}

// 5. 日志服务实现
package com.study.collect.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CollectionLogService {
    private final CollectionMachineLogRepository machineLogRepository;
    private final CollectionTaskLogRepository taskLogRepository;
    private final CollectionTraceLogRepository traceLogRepository;
    
    public CollectionLogService(
            CollectionMachineLogRepository machineLogRepository,
            CollectionTaskLogRepository taskLogRepository,
            CollectionTraceLogRepository traceLogRepository) {
        this.machineLogRepository = machineLogRepository;
        this.taskLogRepository = taskLogRepository;
        this.traceLogRepository = traceLogRepository;
    }

    // 记录机器状态
    public CollectionMachineLog logMachineStatus(String machineId) {
        CollectionMachineLog machineLog = new CollectionMachineLog();
        machineLog.setMachineId(machineId);
        machineLog.setMachineIp(NetworkUtil.getLocalIp());
        machineLog.setMachineName(NetworkUtil.getHostName());
        
        // 获取系统指标
        machineLog.setCpuUsage(SystemResourceUtil.getCpuUsage());
        machineLog.setMemoryUsage(SystemResourceUtil.getMemoryUsage());
        machineLog.setDiskUsage(SystemResourceUtil.getDiskUsage());
        
        machineLog.setLastHeartbeatTime(new Date());
        machineLog.setCreateTime(new Date());
        machineLog.setUpdateTime(new Date());
        
        return machineLogRepository.save(machineLog);
    }

    // 记录任务执行
    public CollectionTaskLog logTaskExecution(String taskId, String collectorId) {
        CollectionTaskLog taskLog = new CollectionTaskLog();
        taskLog.setTaskId(taskId);
        taskLog.setCollectorId(collectorId);
        taskLog.setMachineId(NetworkUtil.getLocalIp());
        taskLog.setStartTime(new Date());
        taskLog.setCreateTime(new Date());
        taskLog.setUpdateTime(new Date());
        
        return taskLogRepository.save(taskLog);
    }

    // 更新任务执行结果
    public void updateTaskResult(String taskLogId, 
                               Integer totalCount,
                               Integer successCount, 
                               Integer failCount,
                               String errorMessage) {
        CollectionTaskLog taskLog = taskLogRepository.findById(taskLogId)
            .orElseThrow(() -> new RuntimeException("Task log not found"));
            
        taskLog.setEndTime(new Date());
        taskLog.setTotalCount(totalCount);
        taskLog.setSuccessCount(successCount);
        taskLog.setFailCount(failCount);
        taskLog.setErrorMessage(errorMessage);
        taskLog.setUpdateTime(new Date());
        
        taskLogRepository.save(taskLog);
    }

    // 记录链路追踪
    public CollectionTraceLog logTrace(String taskId, 
                                     String traceId,
                                     String stage,
                                     String requestInfo,
                                     String responseInfo,
                                     Long costTime) {
        CollectionTraceLog traceLog = new CollectionTraceLog();
        traceLog.setTaskId(taskId);
        traceLog.setTraceId(traceId);
        traceLog.setStage(stage);
        traceLog.setTimestamp(new Date());
        traceLog.setRequestInfo(requestInfo);
        traceLog.setResponseInfo(responseInfo);
        traceLog.setCostTime(costTime);
        
        return traceLogRepository.save(traceLog);
    }

    // 查询任务执行历史
    public List<CollectionTaskLog> getTaskHistory(String taskId) {
        return taskLogRepository.findByTaskId(taskId);
    }

    // 查询链路追踪
    public List<CollectionTraceLog> getTraceHistory(String traceId) {
        return traceLogRepository.findByTraceId(traceId);
    }
}

// 6. 日志切面实现
package com.study.collect.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j 
public class CollectionLogAspect {
    private final CollectionLogService logService;
    
    public CollectionLogAspect(CollectionLogService logService) {
        this.logService = logService;
    }
    
    @Around("@annotation(com.study.collect.annotation.CollectionTrace)")
    public Object traceCollection(ProceedingJoinPoint joinPoint) throws Throwable {
        String taskId = extractTaskId(joinPoint);
        String traceId = UUID.randomUUID().toString();
        String stage = joinPoint.getSignature().getName();
        
        long startTime = System.currentTimeMillis();
        String requestInfo = JsonUtils.toJson(joinPoint.getArgs());
        
        try {
            Object result = joinPoint.proceed();
            long costTime = System.currentTimeMillis() - startTime;
            
            logService.logTrace(
                taskId,
                traceId, 
                stage,
                requestInfo,
                JsonUtils.toJson(result),
                costTime
            );
            
            return result;
        } catch (Throwable e) {
            long costTime = System.currentTimeMillis() - startTime;
            logService.logTrace(
                taskId,
                traceId,
                stage,
                requestInfo,
                ExceptionUtils.getStackTrace(e),
                costTime
            );
            throw e;
        }
    }
    
    private String extractTaskId(ProceedingJoinPoint joinPoint) {
        // 从方法参数中提取taskId的逻辑
        return ""; 
    }
}

```

这是日志系统的基础实现。主要包含以下几个部分：

1. 三种核心日志实体:
    - CollectionMachineLog: 记录采集机器状态
    - CollectionTaskLog: 记录任务执行情况
    - CollectionTraceLog: 记录采集链路追踪

2. MongoDB Repository接口:
    - 提供基础的CRUD操作
    - 添加自定义查询方法

3. 日志服务实现:
    - 记录机器状态
    - 记录任务执行
    - 记录链路追踪
    - 提供日志查询接口

4. 日志切面:
    - 自动记录方法执行情况
    - 捕获异常并记录
    - 计算执行时间

接下来，我们可以继续实现：
1. 任务重复处理机制
2. 优先级队列配置
3. 分布式执行
4. 监控告警

你想先了解哪个部分的实现？