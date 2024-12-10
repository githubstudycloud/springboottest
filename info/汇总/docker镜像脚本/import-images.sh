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