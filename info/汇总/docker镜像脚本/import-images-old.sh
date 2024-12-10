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