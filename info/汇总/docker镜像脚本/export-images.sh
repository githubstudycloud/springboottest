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