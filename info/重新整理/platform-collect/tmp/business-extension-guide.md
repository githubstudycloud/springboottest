# 业务扩展架构指南

## 一、简单业务扩展(直接复用基础架构)

在com.study.collect目录下添加business包，遵循原有架构:

```
com.study.collect/
    └── business/                      # 业务包
        ├── enterprise/                # 具体业务模块(如企业数据采集)
        │   ├── api/                   # API层
        │   │   ├── controller/       
        │   │   │   ├── EnterpriseCollectController.java
        │   │   │   └── EnterpriseStatsController.java
        │   │   └── model/           
        │   │       ├── request/
        │   │       │   └── EnterpriseCollectRequest.java
        │   │       └── response/
        │   │           └── EnterpriseDataVO.java
        │   ├── domain/               # 领域层
        │   │   ├── entity/
        │   │   │   └── EnterpriseData.java
        │   │   └── service/
        │   │       └── EnterpriseCollectService.java
        │   └── infrastructure/       # 基础设施层
        │       └── repository/
        │           └── EnterpriseRepository.java
        └── finance/                  # 另一个业务模块(如金融数据采集)
            └── ...

注意点:
1. 直接继承基础collector、processor等组件
2. 只需实现业务特有的entity、service等
3. 复用基础设施层的MongoDB、Redis等组件
```

## 二、复杂业务扩展(部分重写基础架构)

针对需要特殊处理的复杂业务场景:

```
com.study.collect/
    └── business/
        └── medical/                   # 医疗数据采集业务
            ├── api/                   # API层
            │   ├── controller/
            │   │   ├── MedicalDataController.java
            │   │   └── PatientDataController.java
            │   └── model/
            │       ├── request/
            │       └── response/
            ├── core/                  # 核心层(重写部分基础组件)
            │   ├── collector/         # 自定义采集器
            │   │   ├── DicomCollector.java
            │   │   └── HL7Collector.java
            │   ├── processor/         # 自定义处理器
            │   │   ├── ImageProcessor.java
            │   │   └── PrivacyProcessor.java
            │   └── engine/           # 自定义引擎
            │       └── MedicalCollectEngine.java
            ├── domain/               # 领域层
            │   ├── entity/
            │   │   ├── Patient.java
            │   │   └── MedicalImage.java
            │   └── service/
            │       └── MedicalCollectService.java
            └── infrastructure/       # 基础设施层
                ├── storage/         # 自定义存储
                │   └── PacsStorage.java
                └── gateway/         # 外部系统网关
                    ├── HisGateway.java
                    └── RisGateway.java

注意点:
1. 可以选择性地重写collector、processor等组件
2. 保持与基础架构的接口兼容
3. 可以添加业务特有的基础设施
```

## 三、完全自定义业务扩展(全部重写)

对于完全不同的业务场景:

```
com.study.collect/
    └── business/
        └── iot/                      # 物联网数据采集
            ├── api/                  # 新的API层
            │   ├── mqtt/            # MQTT接口
            │   └── websocket/       # WebSocket接口
            ├── core/                 # 全新的核心层
            │   ├── protocol/        # 协议处理
            │   │   ├── mqtt/
            │   │   └── coap/
            │   ├── codec/           # 编解码
            │   └── gateway/         # 设备网关
            ├── domain/              # 领域层
            │   ├── device/         # 设备管理
            │   └── message/        # 消息处理
            └── infrastructure/      # 基础设施层
                ├── timeseries/     # 时序数据库
                └── streaming/      # 流处理引擎

注意点:
1. 完全独立的业务架构
2. 仍然遵循整体的分层理念
3. 可以引入新的技术组件
```

## 四、实现步骤

1. 选择合适的扩展方式
    - 简单业务：直接复用基础组件
    - 部分复杂：重写必要组件
    - 完全自定义：构建独立架构

2. 创建业务模块
    ```java
    @Configuration
    @ComponentScan("com.study.collect.business.xxx")
    public class BusinessConfig {
        // 业务模块配置
    }
    ```

3. 继承或实现基础组件
    ```java
    // 简单继承
    public class EnterpriseCollector extends AbstractCollector {
        // 实现企业数据采集逻辑
    }

    // 重写实现
    public class DicomCollector implements Collector {
        // 实现DICOM图像采集逻辑
    }
    ```

4. 注册到基础框架
    ```java
    @Component
    public class BusinessCollectorRegistrar implements CollectorRegistry {
        // 注册业务采集器
    }
    ```

## 五、扩展建议

1. 公共组件复用
    - 优先复用基础组件
    - 必要时才重写实现
    - 保持接口兼容性

2. 业务隔离
    - 不同业务独立封装
    - 避免跨业务耦合
    - 合理划分边界

3. 性能优化
    - 针对业务特点优化
    - 实现定制化缓存
    - 优化采集策略
