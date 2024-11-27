我来写一个脚本来实现这个功能。

https://claude.ai/chat/820ba08d-cf2c-48d9-a1ab-a6f80bc2de54

```bash
#!/bin/bash

# 设置日志颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 定义日志函数
log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 显示帮助信息
show_help() {
    echo "Usage: $0 [OPTIONS]"
    echo "Export Docker images to tar.gz files"
    echo
    echo "Options:"
    echo "  -n, --names NAMES       Export images by names (comma-separated)"
    echo "  -i, --image-ids IDS     Export images by IDs (comma-separated)"
    echo "  -c, --container-ids IDS Export images from container IDs (comma-separated)"
    echo "  -C, --container-names NAMES Export images from container names (comma-separated)"
    echo "  -o, --output DIR        Output directory (default: current directory)"
    echo "  -a, --all              Export all images"
    echo "  -h, --help             Show this help message"
    echo
    echo "Examples:"
    echo "  $0 -n nginx:latest,redis:alpine"
    echo "  $0 -i abc123,def456"
    echo "  $0 -c container1,container2"
    echo "  $0 -C my-nginx,my-redis"
    echo "  $0 -a"
}

# 设置默认值
OUTPUT_DIR="$(pwd)/docker_images_$(date +%Y%m%d_%H%M%S)"
EXPORT_ALL=false

# 获取镜像列表函数
get_images_by_names() {
    local names=$1
    local image_list=""
    IFS=',' read -ra NAMES <<< "$names"
    for name in "${NAMES[@]}"; do
        if docker images --format "{{.Repository}}:{{.Tag}}" | grep -q "^${name}$"; then
            image_list="${image_list} ${name}"
        else
            log_warn "Image not found: $name"
        fi
    done
    echo $image_list
}

get_images_by_ids() {
    local ids=$1
    local image_list=""
    IFS=',' read -ra IDS <<< "$ids"
    for id in "${IDS[@]}"; do
        if docker images --format "{{.ID}}" | grep -q "^${id}"; then
            image_list="${image_list} ${id}"
        else
            log_warn "Image ID not found: $id"
        fi
    done
    echo $image_list
}

get_images_from_containers() {
    local containers=$1
    local image_list=""
    IFS=',' read -ra CONTAINERS <<< "$containers"
    for container in "${CONTAINERS[@]}"; do
        local image=$(docker inspect --format='{{.Image}}' $container 2>/dev/null)
        if [ $? -eq 0 ]; then
            image_list="${image_list} ${image}"
        else
            log_warn "Container not found: $container"
        fi
    done
    echo $image_list
}

# 解析命令行参数
PARAMS=""
while (( "$#" )); do
    case "$1" in
        -n|--names)
            if [ -n "$2" ] && [ ${2:0:1} != "-" ]; then
                IMAGE_NAMES=$2
                shift 2
            else
                log_error "Error: Argument for $1 is missing"
                exit 1
            fi
            ;;
        -i|--image-ids)
            if [ -n "$2" ] && [ ${2:0:1} != "-" ]; then
                IMAGE_IDS=$2
                shift 2
            else
                log_error "Error: Argument for $1 is missing"
                exit 1
            fi
            ;;
        -c|--container-ids)
            if [ -n "$2" ] && [ ${2:0:1} != "-" ]; then
                CONTAINER_IDS=$2
                shift 2
            else
                log_error "Error: Argument for $1 is missing"
                exit 1
            fi
            ;;
        -C|--container-names)
            if [ -n "$2" ] && [ ${2:0:1} != "-" ]; then
                CONTAINER_NAMES=$2
                shift 2
            else
                log_error "Error: Argument for $1 is missing"
                exit 1
            fi
            ;;
        -o|--output)
            if [ -n "$2" ] && [ ${2:0:1} != "-" ]; then
                OUTPUT_DIR=$2
                shift 2
            else
                log_error "Error: Argument for $1 is missing"
                exit 1
            fi
            ;;
        -a|--all)
            EXPORT_ALL=true
            shift
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        -*|--*=)
            log_error "Error: Unsupported flag $1"
            exit 1
            ;;
        *)
            PARAMS="$PARAMS $1"
            shift
            ;;
    esac
done

# 创建输出目录
mkdir -p "$OUTPUT_DIR"
if [ $? -ne 0 ]; then
    log_error "Failed to create output directory: $OUTPUT_DIR"
    exit 1
fi

# 收集要导出的镜像
IMAGES_TO_EXPORT=""

if [ "$EXPORT_ALL" = true ]; then
    IMAGES_TO_EXPORT=$(docker images --format "{{.Repository}}:{{.Tag}}" | grep -v "<none>")
else
    # 根据不同的输入参数收集镜像
    if [ -n "$IMAGE_NAMES" ]; then
        IMAGES_TO_EXPORT="$IMAGES_TO_EXPORT $(get_images_by_names "$IMAGE_NAMES")"
    fi
    if [ -n "$IMAGE_IDS" ]; then
        IMAGES_TO_EXPORT="$IMAGES_TO_EXPORT $(get_images_by_ids "$IMAGE_IDS")"
    fi
    if [ -n "$CONTAINER_IDS" ]; then
        IMAGES_TO_EXPORT="$IMAGES_TO_EXPORT $(get_images_from_containers "$CONTAINER_IDS")"
    fi
    if [ -n "$CONTAINER_NAMES" ]; then
        IMAGES_TO_EXPORT="$IMAGES_TO_EXPORT $(get_images_from_containers "$CONTAINER_NAMES")"
    fi
fi

# 检查是否有镜像要导出
if [ -z "$IMAGES_TO_EXPORT" ]; then
    log_error "No images to export"
    exit 1
fi

# 创建导出日志文件
LOG_FILE="$OUTPUT_DIR/export.log"
echo "Export started at $(date)" > "$LOG_FILE"

# 导出镜像
export_count=0
for image in $IMAGES_TO_EXPORT; do
    log_info "Exporting $image..."
    
    # 替换镜像名中的斜杠和冒号为下划线
    filename=$(echo "$image" | tr '/' '_' | tr ':' '_')
    
    # 导出镜像
    docker save "$image" -o "$OUTPUT_DIR/${filename}.tar"
    if [ $? -eq 0 ]; then
        log_info "Successfully exported $image to ${filename}.tar"
        echo "Exported: $image -> ${filename}.tar" >> "$LOG_FILE"
        ((export_count++))
    else
        log_error "Failed to export $image"
        echo "Failed: $image" >> "$LOG_FILE"
    fi
done

# 压缩整个目录
cd $(dirname "$OUTPUT_DIR")
tar czf "${OUTPUT_DIR}.tar.gz" $(basename "$OUTPUT_DIR")
if [ $? -eq 0 ]; then
    log_info "Successfully created archive: ${OUTPUT_DIR}.tar.gz"
else
    log_error "Failed to create archive"
fi

# 输出统计信息
log_info "Export completed: $export_count images exported"
log_info "Archive location: ${OUTPUT_DIR}.tar.gz"
log_info "Log file: $LOG_FILE"

```

使用方法：

1. 保存脚本并赋予执行权限：
```bash
chmod +x export-images.sh
```

2. 使用示例：
```bash
# 导出指定名称的镜像
./export-images.sh -n nginx:latest,redis:alpine

# 导出指定ID的镜像
./export-images.sh -i abc123,def456

# 导出指定容器使用的镜像（通过容器ID）
./export-images.sh -c container1,container2

# 导出指定容器使用的镜像（通过容器名称）
./export-images.sh -C my-nginx,my-redis

# 导出所有镜像
./export-images.sh -a

# 指定输出目录
./export-images.sh -a -o /path/to/output

# 显示帮助信息
./export-images.sh -h
```

脚本特性：
1. 支持多种方式指定要导出的镜像
2. 自动创建带时间戳的输出目录
3. 生成详细的导出日志
4. 自动压缩导出的文件
5. 彩色日志输出
6. 完整的错误处理
7. 帮助文档

输出内容：
1. 每个镜像会导出为单独的 .tar 文件
2. 所有文件会被打包成一个 .tar.gz 文件
3. 包含导出日志文件
4. 显示实时导出进度
5. 最终显示统计信息
   这个错误是因为脚本使用了 bash 特性但用 sh 来执行。让我们修改一下执行方式。

1. 确认脚本的第一行包含了正确的解释器声明：
```bash
#!/bin/bash
```

2. 有两种方式执行脚本：

方式一：使用 bash 命令执行
```bash
bash ./export-images.sh -h
```

方式二：给脚本添加执行权限后直接执行
```bash
chmod +x ./export-images.sh
./export-images.sh -h
```

避免使用：
```bash
sh ./export-images.sh -h  # 不要这样使用
```

这是因为在许多系统中，sh 实际上是指向 dash 的符号链接，而 dash 不支持某些 bash 特性。如果你运行：
```bash
ls -l /bin/sh
```
可能会看到它是指向 dash 的符号链接。

所以，要么：
1. 使用 bash 命令执行
2. 或给予执行权限后直接运行
3. 确保脚本第一行是 #!/bin/bash
   我来写一个对应的镜像导入脚本。



```bash
#!/bin/bash

# 设置日志颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 定义日志函数
log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 显示帮助信息
show_help() {
    echo "Usage: $0 [OPTIONS] [FILE/DIR]..."
    echo "Import Docker images from tar.gz archives"
    echo
    echo "Options:"
    echo "  -f, --file FILE        Specific tar.gz file to import"
    echo "  -d, --dir DIR          Directory containing tar.gz files"
    echo "  -p, --pattern PATTERN  File pattern to match (e.g., '*.tar.gz')"
    echo "  -h, --help             Show this help message"
    echo
    echo "Examples:"
    echo "  $0                     # Import from current directory"
    echo "  $0 -f images.tar.gz"
    echo "  $0 -d /path/to/images"
    echo "  $0 -p 'docker_*.tar.gz'"
}

# 导入单个tar文件
import_tar() {
    local file=$1
    local base_name=$(basename "$file")
    
    log_info "Importing from $base_name..."
    docker load -i "$file"
    
    if [ $? -eq 0 ]; then
        log_info "Successfully imported $base_name"
        echo "Imported: $base_name" >> "$LOG_FILE"
        return 0
    else
        log_error "Failed to import $base_name"
        echo "Failed: $base_name" >> "$LOG_FILE"
        return 1
    fi
}

# 解压并导入tar.gz文件
process_targz() {
    local targz=$1
    local base_name=$(basename "$targz")
    local temp_dir=$(mktemp -d)
    
    log_info "Processing $base_name..."
    
    # 解压tar.gz文件
    tar xzf "$targz" -C "$temp_dir"
    if [ $? -ne 0 ]; then
        log_error "Failed to extract $base_name"
        rm -rf "$temp_dir"
        return 1
    fi
    
    # 遍历所有.tar文件并导入
    find "$temp_dir" -name "*.tar" -type f | while read tar_file; do
        import_tar "$tar_file"
    done
    
    # 清理临时目录
    rm -rf "$temp_dir"
}

# 初始化变量
IMPORT_DIR="."
PATTERN="*.tar.gz"
SPECIFIC_FILES=()
LOG_FILE="docker_import_$(date +%Y%m%d_%H%M%S).log"

# 解析命令行参数
while (( "$#" )); do
    case "$1" in
        -f|--file)
            if [ -n "$2" ] && [ ${2:0:1} != "-" ]; then
                SPECIFIC_FILES+=("$2")
                shift 2
            else
                log_error "Error: Argument for $1 is missing"
                exit 1
            fi
            ;;
        -d|--dir)
            if [ -n "$2" ] && [ ${2:0:1} != "-" ]; then
                IMPORT_DIR="$2"
                shift 2
            else
                log_error "Error: Argument for $1 is missing"
                exit 1
            fi
            ;;
        -p|--pattern)
            if [ -n "$2" ] && [ ${2:0:1} != "-" ]; then
                PATTERN="$2"
                shift 2
            else
                log_error "Error: Argument for $1 is missing"
                exit 1
            fi
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        -*|--*=)
            log_error "Error: Unsupported flag $1"
            exit 1
            ;;
        *)
            SPECIFIC_FILES+=("$1")
            shift
            ;;
    esac
done

# 初始化日志文件
echo "Import started at $(date)" > "$LOG_FILE"
echo "Working directory: $(pwd)" >> "$LOG_FILE"

# 处理指定的文件
if [ ${#SPECIFIC_FILES[@]} -gt 0 ]; then
    for file in "${SPECIFIC_FILES[@]}"; do
        if [ -f "$file" ]; then
            process_targz "$file"
        else
            log_error "File not found: $file"
        fi
    done
else
    # 处理目录中的所有匹配文件
    if [ ! -d "$IMPORT_DIR" ]; then
        log_error "Directory not found: $IMPORT_DIR"
        exit 1
    fi
    
    cd "$IMPORT_DIR"
    for targz in $PATTERN; do
        if [ -f "$targz" ]; then
            process_targz "$targz"
        fi
    done
fi

# 统计导入结果
TOTAL_IMPORTED=$(grep "^Imported:" "$LOG_FILE" | wc -l)
TOTAL_FAILED=$(grep "^Failed:" "$LOG_FILE" | wc -l)

# 输出统计信息
log_info "Import completed:"
log_info "  Successfully imported: $TOTAL_IMPORTED"
if [ $TOTAL_FAILED -gt 0 ]; then
    log_warn "  Failed imports: $TOTAL_FAILED"
fi
log_info "Log file: $LOG_FILE"

```

使用方法：

1. 保存脚本并赋予执行权限：
```bash
chmod +x import-images.sh
```

2. 使用示例：
```bash
# 从当前目录导入所有 tar.gz 文件
./import-images.sh

# 导入指定的 tar.gz 文件
./import-images.sh -f images.tar.gz

# 导入指定目录中的所有 tar.gz 文件
./import-images.sh -d /path/to/images

# 导入指定模式的文件
./import-images.sh -p 'docker_*.tar.gz'

# 导入多个指定文件
./import-images.sh file1.tar.gz file2.tar.gz

# 显示帮助信息
./import-images.sh -h
```

脚本特性：
1. 支持多种导入方式
2. 自动解压 tar.gz 文件
3. 生成详细的导入日志
4. 显示实时导入进度
5. 提供导入统计信息
6. 彩色日志输出
7. 完整的错误处理
8. 清理临时文件

注意事项：
1. 确保有足够的磁盘空间用于解压文件
2. 确保对目标目录有读写权限
3. 大文件导入可能需要较长时间
4. 建议保留导入日志以便追踪问题