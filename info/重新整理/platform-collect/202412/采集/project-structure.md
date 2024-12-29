# 测试用例采集模块结构

```
com.study.collect/
├── business/
│   └── testcase/                          # 测试用例业务模块
│       ├── api/                           # API层
│       │   ├── controller/               
│       │   │   ├── TestCaseController.java    # 测试用例控制器
│       │   │   └── VersionController.java      # 版本控制器
│       │   └── model/                    
│       │       ├── request/              
│       │       │   ├── TestCaseCollectRequest.java  # 采集请求
│       │       │   └── VersionCollectRequest.java   # 版本采集请求
│       │       └── response/             
│       │           ├── TestCaseVO.java         # 测试用例视图对象
│       │           └── VersionVO.java          # 版本视图对象
│       │
│       ├── core/                          # 核心层
│       │   ├── collector/                # 采集器
│       │   │   ├── VersionCollector.java       # 版本采集器
│       │   │   └── TestCaseCollector.java      # 测试用例采集器
│       │   ├── strategy/                 # 策略
│       │   │   ├── CollectStrategy.java        # 策略接口
│       │   │   ├── MainVersionStrategy.java    # 主干版本策略
│       │   │   ├── BranchVersionStrategy.java  # 分支版本策略
│       │   │   └── CollectStrategyFactory.java # 策略工厂
│       │   └── processor/                # 处理器
│       │       ├── TestCaseProcessor.java      # 用例处理器
│       │       └── VersionProcessor.java       # 版本处理器
│       │
│       ├── domain/                        # 领域层
│       │   ├── entity/                   
│       │   │   ├── data/                 # 数据实体(与API对应)
│       │   │   │   ├── TestCaseData.java      # 测试用例数据
│       │   │   │   ├── VersionData.java       # 版本数据
│       │   │   │   └── SubVersionData.java    # 子版本数据
│       │   │   └── model/                # 业务实体
│       │   │       ├── TestCaseInfo.java      # 测试用例信息
│       │   │       ├── VersionInfo.java       # 版本信息
│       │   │       └── SubVersionInfo.java    # 子版本信息
│       │   ├── repository/               
│       │   │   ├── TestCaseRepository.java    # 用例仓储接口
│       │   │   └── VersionRepository.java     # 版本仓储接口
│       │   └── service/                  
│       │       ├── VersionTestCaseService.java # 版本用例服务
│       │       └── CollectService.java        # 采集服务
│       │
│       ├── infrastructure/                # 基础设施层
│       │   ├── config/                   
│       │   │   ├── TestCaseApiConfig.java     # API配置
│       │   │   └── CollectorConfig.java       # 采集器配置
│       │   ├── repository/               
│       │   │   ├── MongoTestCaseRepository.java  # MongoDB实现
│       │   │   └── MongoVersionRepository.java   # MongoDB实现
│       │   ├── http/                     
│       │   │   ├── HttpUtil.java             # HTTP工具类
│       │   │   └── ApiUrlBuilder.java        # URL构建器
│       │   └── convert/                  
│       │       └── TestCaseConvert.java      # 数据转换器
│       │
│       └── common/                        # 公共模块
           ├── constant/                  
           │   └── TestCaseConstant.java      # 常量定义
           ├── enums/                     
           │   ├── VersionType.java           # 版本类型枚举
           │   └── CollectType.java           # 采集类型枚举
           ├── model/                     
           │   ├── CollectContext.java        # 采集上下文
           │   ├── CollectResult.java         # 采集结果
           │   └── VersionCollectScope.java   # 采集范围
           └── utils/                     
               └── CollectUtil.java           # 工具类

└── resources/                            # 资源文件
    ├── application.yml                   # 应用配置
    └── application-test.yml              # 测试配置
```

## 主要组件说明

### 1. 数据模型设计
- **数据实体(XXXData)**：与外部API响应结构对应
- **业务实体(XXXInfo)**：包含业务属性和逻辑
- **视图对象(XXXVO)**：用于API层数据传输

### 2. 核心功能组件
- **采集器**：负责数据采集的核心逻辑
- **策略**：实现不同类型版本的采集策略
- **处理器**：处理采集数据的转换和处理

### 3. 配置管理
- **TestCaseApiConfig**：API相关配置
- **CollectorConfig**：采集器相关配置

### 4. 采集范围控制
- **VersionCollectScope**：控制采集范围和类型
- **CollectStrategy**：不同版本类型的采集策略

### 5. 基础设施支持
- **HttpUtil**：HTTP请求工具
- **ApiUrlBuilder**：统一的URL构建
- **MongoDB存储实现**：数据持久化

## 关键功能点

1. 版本管理
- 支持主干版本管理
- 支持分支版本管理
- 版本关系维护

2. 采集控制
- 支持全量采集
- 支持增量采集
- 支持范围控制

3. 并发处理
- 主干分支并发
- 批量数据并发
- 可配置并发度

4. 扩展能力
- 策略模式支持扩展
- 配置化的参数管理
- 模块化的设计结构

## 配置示例

```yaml
testcase:
  api:
    baseUrl: http://api.example.com
    version:
      listPath: /api/version/list
      detailPath: /api/version/detail
      pageSize: 500
      maxConcurrent: 10
    testCase:
      listPath: /api/testcase/list
      detailPath: /api/testcase/detail
      pageSize: 500
      maxConcurrent: 10
```
