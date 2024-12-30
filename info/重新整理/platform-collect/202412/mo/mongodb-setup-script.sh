#!/bin/bash

# 创建必要的目录
mkdir -p data/mongo{1,2,3}
mkdir -p logs

# 启动容器
docker-compose up -d

# 等待容器启动完成
sleep 30

# 初始化副本集
docker exec -it mongo1 mongosh --eval "
config = {
    '_id': 'rs0',
    'members': [
        {'_id': 0, 'host': 'mongo1:27017', 'priority': 2},
        {'_id': 1, 'host': 'mongo2:27017', 'priority': 1},
        {'_id': 2, 'host': 'mongo3:27017', 'priority': 1}
    ]
};
rs.initiate(config);
"

# 等待副本集初始化
sleep 10

# 检查副本集状态
docker exec -it mongo1 mongosh --eval "rs.status()"

echo "MongoDB复制集已启动完成！"
echo "Primary节点: localhost:27017"
echo "Secondary节点: localhost:27018"
echo "Secondary节点: localhost:27019"

# 清理脚本
cat > cleanup.sh <<EOF
#!/bin/bash

# 删除30天前的备份
find data/backup -mtime +30 -delete

# 压缩日志文件
find logs -name "*.log" -mtime +7 -exec gzip {} \;
EOF

chmod +x cleanup.sh

# 添加定时任务
(crontab -l 2>/dev/null; echo "0 0 * * * $(pwd)/cleanup.sh") | crontab -