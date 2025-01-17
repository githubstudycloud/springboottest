```java
// TaskEntity.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("task_record")
public class TaskEntity implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("task_type")
    private String taskType;
    
    @TableField("version")
    private String version;
    
    @TableField("method_name") 
    private String methodName;
    
    @TableField("method_params")
    private String methodParams;
    
    @TableField("status")
    private String status; // INIT, RUNNING, SUCCESS, FAILED
    
    @TableField("error_msg")
    private String errorMsg;
    
    @TableField("retry_count")
    private Integer retryCount;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

// TaskMapper.java
@Mapper
public interface TaskMapper {
    
    @Insert("INSERT INTO task_record(task_type, version, method_name, method_params, status, create_time, update_time) " +
            "VALUES(#{taskType}, #{version}, #{methodName}, #{methodParams}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TaskEntity task);

    @Update("UPDATE task_record SET status = #{status}, error_msg = #{errorMsg}, " +
            "retry_count = #{retryCount}, update_time = #{updateTime} " +
            "WHERE id = #{id}")
    int update(TaskEntity task);

    @Select("SELECT * FROM task_record WHERE task_type = #{taskType} AND version = #{version} " +
            "AND method_name = #{methodName} AND method_params = #{methodParams} " +
            "ORDER BY create_time DESC LIMIT 1")
    @Results({
            @Result(column = "task_type", property = "taskType"),
            @Result(column = "method_name", property = "methodName"),
            @Result(column = "method_params", property = "methodParams"),
            @Result(column = "error_msg", property = "errorMsg"),
            @Result(column = "retry_count", property = "retryCount"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime")
    })
    TaskEntity queryLatestTask(@Param("taskType") String taskType,
                             @Param("version") String version,
                             @Param("methodName") String methodName,
                             @Param("methodParams") String methodParams);
}

// TaskParamsWrapper.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskParamsWrapper implements Serializable {
    private String taskType;
    private String version;
    private String methodName;
    private Map<String, Object> params;
    
    private static final long serialVersionUID = 1L;
}

// TaskService.java
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper taskMapper;
    private final ObjectMapper objectMapper;
    
    private static final String STATUS_INIT = "INIT";
    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";
    
    @Transactional(rollbackFor = Exception.class)
    public <T> T executeTask(TaskParamsWrapper wrapper, Function<Map<String, Object>, T> executeFunction) {
        // 1. 参数校验
        validateParams(wrapper);
        
        // 2. 检查是否存在执行中的任务
        String paramsJson = convertParamsToJson(wrapper.getParams());
        TaskEntity existingTask = taskMapper.queryLatestTask(
                wrapper.getTaskType(),
                wrapper.getVersion(), 
                wrapper.getMethodName(),
                paramsJson
        );
        
        if (existingTask != null) {
            // 如果存在执行中或已完成的任务，直接返回
            if (STATUS_RUNNING.equals(existingTask.getStatus())) {
                log.info("Task is already running, taskId: {}", existingTask.getId());
                throw new RuntimeException("Task is already running");
            }
            if (STATUS_SUCCESS.equals(existingTask.getStatus())) {
                log.info("Task already executed successfully, taskId: {}", existingTask.getId());
                return null;
            }
        }
        
        // 3. 创建新任务记录
        TaskEntity newTask = createTask(wrapper, paramsJson);
        
        try {
            // 4. 执行任务
            T result = executeFunction.apply(wrapper.getParams());
            
            // 5. 更新任务状态为成功
            updateTaskStatus(newTask, STATUS_SUCCESS, null);
            
            return result;
            
        } catch (Exception e) {
            // 6. 更新任务状态为失败
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Task execution failed";
            updateTaskStatus(newTask, STATUS_FAILED, errorMsg);
            throw new RuntimeException("Task execution failed", e);
        }
    }
    
    private void validateParams(TaskParamsWrapper wrapper) {
        if (StringUtils.isEmpty(wrapper.getTaskType())) {
            throw new IllegalArgumentException("taskType cannot be empty");
        }
        if (StringUtils.isEmpty(wrapper.getVersion())) {
            throw new IllegalArgumentException("version cannot be empty");
        }
        if (StringUtils.isEmpty(wrapper.getMethodName())) {
            throw new IllegalArgumentException("methodName cannot be empty");
        }
        if (wrapper.getParams() == null) {
            throw new IllegalArgumentException("params cannot be null");
        }
    }
    
    private String convertParamsToJson(Map<String, Object> params) {
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert params to JSON", e);
        }
    }
    
    private TaskEntity createTask(TaskParamsWrapper wrapper, String paramsJson) {
        TaskEntity task = TaskEntity.builder()
                .taskType(wrapper.getTaskType())
                .version(wrapper.getVersion())
                .methodName(wrapper.getMethodName())
                .methodParams(paramsJson)
                .status(STATUS_INIT)
                .retryCount(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
                
        taskMapper.insert(task);
        return task;
    }
    
    private void updateTaskStatus(TaskEntity task, String status, String errorMsg) {
        task.setStatus(status);
        task.setErrorMsg(errorMsg);
        task.setUpdateTime(LocalDateTime.now());
        if (STATUS_FAILED.equals(status)) {
            task.setRetryCount(task.getRetryCount() + 1);
        }
        taskMapper.update(task);
    }
}

// schema.sql
CREATE TABLE `task_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_type` varchar(64) NOT NULL COMMENT '任务类型',
  `version` varchar(32) NOT NULL COMMENT '版本号',
  `method_name` varchar(128) NOT NULL COMMENT '方法名',
  `method_params` text COMMENT '方法参数(JSON)',
  `status` varchar(16) NOT NULL COMMENT '状态',
  `error_msg` text COMMENT '错误信息',
  `retry_count` int(11) DEFAULT '0' COMMENT '重试次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_method` (`task_type`,`version`,`method_name`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务记录表';
```

// Example usage:
```java
@Autowired
private TaskService taskService;

public void example() {
    // 构建任务参数
    TaskParamsWrapper wrapper = TaskParamsWrapper.builder()
        .taskType("DATA_SYNC")
        .version("v1.0")
        .methodName("syncData")
        .params(Map.of(
            "sourceId", 123,
            "targetId", 456,
            "syncTime", LocalDateTime.now()
        ))
        .build();
    
    // 执行任务
    String result = taskService.executeTask(wrapper, params -> {
        // 实际的业务处理逻辑
        log.info("Processing task with params: {}", params);
        // ... do something
        return "Task completed";
    });
}
```