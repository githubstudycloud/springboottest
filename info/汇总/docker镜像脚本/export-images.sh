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