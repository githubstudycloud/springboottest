# Docker镜像部署指南（完整版）

## 一、镜像清单

### 1. 管理和监控工具
| 组件 | 镜像 | 端口 | 大小 |
|------|------|------|------|
| Portainer | portainer/portainer-ce:latest | 9000, 9443 | ~80MB |
| Nginx Proxy Manager | jc21/nginx-proxy-manager:latest | 80, 81, 443 | ~350MB |
| DPanel | dpanel/dpanel:latest | 9999 | ~150MB |
| GitLab | gitlab/gitlab-ce:latest | 80, 443, 22 | ~2GB |

### 2. 基础环境
| 组件 | 镜像 | 端口 | 大小 |
|------|------|------|------|
| JDK 21(JRE) | eclipse-temurin:21-jre | N/A | ~200MB |
| OpenJDK 21(完整版) | eclipse-temurin:21 | N/A | ~400MB |
| Python | python:3.11-slim | N/A | ~120MB |

### 3. 微服务组件
| 组件 | 镜像 | 端口 | 大小 |
|------|------|------|------|
| Nacos | nacos/nacos-server:v2.3.1 | 8848, 9848, 9849 | ~1GB |

### 4. 数据库
| 组件 | 镜像 | 端口 | 大小 |
|------|------|------|------|
| MySQL 5 | mysql:5.7 | 3306 | ~450MB |
| MySQL 8 | mysql:8.0 | 3306 | ~550MB |
| PostgreSQL | postgres:16 | 5432 | ~400MB |
| ClickHouse | clickhouse/clickhouse-server:latest | 8123, 9000 | ~500MB |
| ZooKeeper | bitnami/zookeeper:latest | 2181, 2888, 3888 | ~400MB |
| MongoDB | mongo:7.0 | 27017 | ~700MB |

### 5. 中间件
| 组件 | 镜像 | 端口 | 大小 |
|------|------|------|------|
| Redis | redis:7.2 | 6379 | ~150MB |
| RabbitMQ | rabbitmq:3.12-management | 5672, 15672 | ~300MB |
| Kafka | bitnami/kafka:latest | 9092 | ~500MB |

### 6. 日志系统
| 组件 | 镜像 | 端口 | 大小 |
|------|------|------|------|
| Graylog | graylog/graylog:5.2 | 9000, 12201 | ~1GB |
| Elasticsearch | elasticsearch:7.17.10 | 9200, 9300 | ~1GB |
| ELK-Elasticsearch | elasticsearch:8.12.0 | 9200, 9300 | ~1.2GB |
| ELK-Logstash | logstash:8.12.0 | 5044 | ~800MB |
| ELK-Kibana | kibana:8.12.0 | 5601 | ~800MB |

### 7. 监控系统
| 组件 | 镜像 | 端口 | 大小 |
|------|------|------|------|
| Grafana | grafana/grafana:latest | 3000 | ~300MB |
| Prometheus | prom/prometheus:latest | 9090 | ~200MB |

## 二、一键下载脚本 (pull-images.sh)

```bash
#!/bin/bash

# 创建日志文件
LOG_FILE="docker_pull.log"
echo "开始拉取镜像 - $(date)" > $LOG_FILE

# 函数：拉取镜像并记录日志
pull_image() {
    echo "正在拉取: $1"
    docker pull $1 >> $LOG_FILE 2>&1
    if [ $? -eq 0 ]; then
        echo "✅ 成功拉取: $1"
    else
        echo "❌ 拉取失败: $1"
    fi
}

# 管理和监控工具
pull_image "portainer/portainer-ce:latest"
pull_image "jc21/nginx-proxy-manager:latest"
pull_image "ddsderek/dpanel:latest"
pull_image "gitlab/gitlab-ce:latest"

# 基础环境
pull_image "eclipse-temurin:21-jre"
pull_image "eclipse-temurin:21"
pull_image "python:3.11-slim"

# 微服务组件
pull_image "nacos/nacos-server:v2.3.1"

# 数据库
pull_image "mysql:5.7"
pull_image "mysql:8.0"
pull_image "postgres:16"
pull_image "clickhouse/clickhouse-server:latest"
pull_image "bitnami/zookeeper:latest"
pull_image "mongo:7.0"

# 中间件
pull_image "redis:7.2"
pull_image "rabbitmq:3.12-management"
pull_image "bitnami/kafka:latest"

# 日志系统
pull_image "graylog/graylog:5.2"
pull_image "elasticsearch:7.17.10"
pull_image "elasticsearch:8.12.0"
pull_image "logstash:8.12.0"
pull_image "kibana:8.12.0"

# 监控系统
pull_image "grafana/grafana:latest"
pull_image "prom/prometheus:latest"

echo "镜像拉取完成 - $(date)" >> $LOG_FILE
echo "查看详细日志: cat $LOG_FILE"
```

[前面部分保持不变，从第三部分开始更新]

## 三、镜像导出导入脚本

### 3.1 导出脚本 (export-images.sh)

```bash
#!/bin/bash

# 设置导出目录
EXPORT_DIR="docker_images_$(date +%Y%m%d)"
mkdir -p $EXPORT_DIR

# 创建日志文件
LOG_FILE="$EXPORT_DIR/export.log"
echo "开始导出镜像 - $(date)" > $LOG_FILE

# 创建镜像大小记录文件
SIZE_FILE="$EXPORT_DIR/images_size.txt"
echo "镜像大小统计 - $(date)" > $SIZE_FILE

# 获取所有镜像列表
images=$(docker images --format "{{.Repository}}:{{.Tag}}" | grep -v "<none>")

# 导出镜像列表
echo "$images" > "$EXPORT_DIR/image_list.txt"

# 计算总大小
total_size=0

# 遍历并导出每个镜像
for img in $images
do
    # 替换斜杠为下划线，用作文件名
    filename=$(echo $img | tr '/' '_' | tr ':' '_')
    
    # 获取镜像大小
    size=$(docker images $img --format "{{.Size}}")
    
    # 记录镜像大小
    echo "$img: $size" >> $SIZE_FILE
    
    echo "正在导出: $img (大小: $size)"
    docker save -o "$EXPORT_DIR/${filename}.tar" $img >> $LOG_FILE 2>&1
    
    if [ $? -eq 0 ]; then
        echo "✅ 成功导出: $img"
    else
        echo "❌ 导出失败: $img"
    fi
done

# 创建说明文件
cat > "$EXPORT_DIR/README.md" << EOF
# Docker镜像包说明

- 导出时间: $(date)
- 镜像总数: $(echo "$images" | wc -l)
- 总大小: $(du -sh $EXPORT_DIR | cut -f1)

## 使用方法

1. 将整个目录复制到目标服务器
2. 进入目录: cd $EXPORT_DIR
3. 运行导入脚本: ./import-images.sh

## 镜像列表

\`\`\`
$(echo "$images")
\`\`\`
EOF

# 创建校验文件
echo "生成文件MD5校验..."
cd $EXPORT_DIR && find . -type f -name "*.tar" -exec md5sum {} \; > checksums.md5

# 打包所有文件
echo "正在打包所有文件..."
cd .. && tar czf "${EXPORT_DIR}.tar.gz" $EXPORT_DIR

echo "导出完成！"
echo "1. 所有文件已保存到: ${EXPORT_DIR}"
echo "2. 压缩包已创建: ${EXPORT_DIR}.tar.gz"
echo "3. 查看导出日志: cat $LOG_FILE"
echo "4. 查看镜像大小统计: cat $SIZE_FILE"
```

### 3.2 导入脚本 (import-images.sh)

```bash
#!/bin/bash

# 创建日志文件
LOG_FILE="import.log"
echo "开始导入镜像 - $(date)" > $LOG_FILE

# 检查MD5校验
check_md5() {
    if [ -f checksums.md5 ]; then
        echo "正在验证文件完整性..."
        md5sum -c checksums.md5 >> $LOG_FILE 2>&1
        if [ $? -ne 0 ]; then
            echo "❌ 文件校验失败，请检查文件完整性"
            exit 1
        fi
        echo "✅ 文件校验通过"
    fi
}

# 导入单个镜像
import_image() {
    local file=$1
    echo "正在导入: $file"
    docker load -i "$file" >> $LOG_FILE 2>&1
    if [ $? -eq 0 ]; then
        echo "✅ 成功导入: $file"
    else
        echo "❌ 导入失败: $file" | tee -a $LOG_FILE
        return 1
    fi
}

# 主要导入流程
main() {
    # 检查目录
    if [ ! -d "$(pwd)" ]; then
        echo "❌ 目录不存在"
        exit 1
    fi

    # 执行MD5校验
    check_md5

    # 获取所有tar文件
    local tar_files=$(find . -name "*.tar")
    local total_files=$(echo "$tar_files" | wc -l)
    local current=0
    local failed=0

    echo "找到 $total_files 个镜像文件待导入"

    # 导入所有镜像
    for file in $tar_files; do
        ((current++))
        echo "[$current/$total_files] 处理中..."
        if ! import_image "$file"; then
            ((failed++))
        fi
    done

    # 导入完成统计
    echo "导入完成！"
    echo "总计: $total_files"
    echo "成功: $((total_files-failed))"
    echo "失败: $failed"
    echo "详细日志请查看: $LOG_FILE"

    # 如果有失败的情况，显示失败列表
    if [ $failed -gt 0 ]; then
        echo "失败的镜像列表："
        grep "导入失败" $LOG_FILE
    fi
}

# 执行主流程
main
```

### 3.3 脚本特性说明

#### 导出脚本 (export-images.sh) 特性：
1. 自动创建带时间戳的导出目录
2. 记录详细的导出日志
3. 统计每个镜像的大小
4. 生成MD5校验文件
5. 自动打包所有文件
6. 生成详细的README文档

#### 导入脚本 (import-images.sh) 特性：
1. MD5完整性校验
2. 详细的导入进度显示
3. 错误处理和日志记录
4. 导入结果统计
5. 失败任务的详细列表

### 3.4 使用方法

1. 导出镜像：
```bash
chmod +x export-images.sh
./export-images.sh
```

2. 导入镜像：
```bash
# 解压（如果是压缩包）
tar xzf docker_images_[日期].tar.gz
cd docker_images_[日期]

# 执行导入
chmod +x import-images.sh
./import-images.sh
```

### 3.5 注意事项
1. 导出过程：
   - 确保有足够的磁盘空间（约30GB）
   - 不要中断导出过程
   - 检查导出日志确认成功

2. 导入过程：
   - 先检查MD5确保文件完整
   - 查看导入日志排查问题
   - 失败的镜像可以单独重试

## 四、使用说明

1. 在线环境下载镜像：
```bash
chmod +x pull-images.sh
./pull-images.sh
```


## 五、注意事项

1. 磁盘空间：
    - 所有镜像总大小约15GB
    - 导出文件需要额外15GB空间
    - 建议预留40GB以上空间

2. 网络要求：
    - 确保能访问Docker Hub
    - 建议使用稳定网络连接
    - 可考虑使用镜像加速器

3. 系统要求：
    - Docker版本 >= 20.10
    - 内存建议 >= 16GB
    - CPU建议 >= 4核

4. 安全建议：
    - 生产环境部署前更改默认密码
    - 做好网络隔离
    - 定期更新镜像版本

5. Graylog 特别说明：
    - 需要 MongoDB (已包含在数据库部分)
    - 需要 Elasticsearch 7.x 版本（注意与ELK使用不同版本）
    - 默认用户名：admin
    - 需要设置密码和密钥

6. 版本兼容性：
    - Graylog 5.2 需要 Elasticsearch 7.17.x
    - ELK Stack 使用 8.12.0 版本
    - 两个版本的 Elasticsearch 不冲突，可以同时运行