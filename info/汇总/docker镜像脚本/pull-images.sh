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