#!/bin/bash

# 清理之前的环境
docker-compose down
rm -rf data/mongo*

# 创建数据目录和密钥文件
mkdir -p data/mongo{1,2,3}
openssl rand -base64 756 > mongod.key
chmod 400 mongod.key

# 启动服务
docker-compose up -d

# 等待服务启动
echo "等待服务启动..."
sleep 30

# 在每个容器中设置密钥文件权限
for container in mongo1 mongo2 mongo3; do
    echo "设置 $container 的密钥文件权限..."
    docker exec -it $container bash -c "
        mkdir -p /data/keys
        cp /data/mongodb.key /data/keys/
        chown -R mongodb:mongodb /data/keys
        chmod 400 /data/keys/mongodb.key
        ls -l /data/keys/mongodb.key
    "
done

# 初始化副本集
echo "初始化副本集..."
docker exec -it mongo1 mongosh --eval '
rs.initiate({
 _id: "rs0",
 members: [
   {_id: 0, host: "mongo1:27017", priority: 2},
   {_id: 1, host: "mongo2:27017", priority: 1},
   {_id: 2, host: "mongo3:27017", priority: 1}
 ]
});'

# 检查副本集状态
echo "检查副本集状态..."
sleep 5
docker exec -it mongo1 mongosh -u admin -p password123 --eval "rs.status()"

echo "MongoDB集群已启动完成！"