import os
import markdown

# 扩展文件扩展名到语言类型的映射
LANGUAGE_MAP = {
    # 通用
    '.txt': 'text',
    '.md': 'markdown',
    '.json': 'json',
    '.xml': 'xml',
    '.yaml': 'yaml',
    '.yml': 'yaml',

    # 编程语言
    '.py': 'python',
    '.java': 'java',
    '.js': 'javascript',
    '.ts': 'typescript',
    '.html': 'html',
    '.css': 'css',
    '.scss': 'scss',
    '.sql': 'sql',
    '.sh': 'bash',
    '.bash': 'bash',
    '.r': 'r',

    # 配置文件
    '.properties': 'properties',
    '.conf': 'conf',
    '.cfg': 'ini',
    '.ini': 'ini',
    '.toml': 'toml',

    # Java 相关
    '.gradle': 'gradle',
    '.groovy': 'groovy',
    '.jsp': 'jsp',

    # Python 相关
    '.ipynb': 'json',  # Jupyter Notebook
    '.pip': 'text',
    '.requirements': 'text',

    # Web 开发
    '.jsx': 'jsx',
    '.tsx': 'tsx',
    '.vue': 'vue',

    # 数据格式
    '.csv': 'csv',

    # Docker 相关
    'Dockerfile': 'dockerfile',
    '.dockerignore': 'text',
    'docker-compose.yml': 'yaml',
    'docker-compose.yaml': 'yaml',

    # 其他构建和配置文件
    'Makefile': 'makefile',
    '.gitignore': 'text',
    '.env': 'text',
    'pom.xml': 'xml',  # Maven
    'build.gradle': 'gradle',  # Gradle
    'settings.gradle': 'gradle',
    'requirements.txt': 'text',  # Python requirements
    'package.json': 'json',  # Node.js
    'tsconfig.json': 'json',  # TypeScript
}

# 要忽略的文件夹和文件类型
# IGNORE_FOLDERS = {'.git', '.idea', 'node_modules', '__pycache__', 'venv', 'target', 'build', 'test'}
# IGNORE_FOLDERS = {'.git', '.idea', 'node_modules', '__pycache__', 'venv', 'target', 'build', 'test','platform-collect','platform-dashboard','platform-gateway'}
IGNORE_FOLDERS = {'.git', '.idea', 'node_modules', '__pycache__', 'venv', 'target', 'build', 'test','platform-scheduler','platform-dashboard','platform-gateway'}
IGNORE_EXTENSIONS = {'.pyc', '.class', '.o', '.log', '.jar', '.war', '.ear'}

def get_language(file_path):
    file_name = os.path.basename(file_path)
    if file_name in LANGUAGE_MAP:
        return LANGUAGE_MAP[file_name]
    _, ext = os.path.splitext(file_name)
    return LANGUAGE_MAP.get(ext.lower(), '')

def should_ignore(path):
    name = os.path.basename(path)
    if name in IGNORE_FOLDERS:
        return True
    _, ext = os.path.splitext(name)
    return ext.lower() in IGNORE_EXTENSIONS

def generate_file_structure(root_dir):
    structure = []
    for root, dirs, files in os.walk(root_dir):
        dirs[:] = [d for d in dirs if not should_ignore(os.path.join(root, d))]
        level = root.replace(root_dir, '').count(os.sep)
        indent = ' ' * 4 * level
        structure.append(f'{indent}{os.path.basename(root)}/')
        subindent = ' ' * 4 * (level + 1)
        for f in files:
            if not should_ignore(os.path.join(root, f)):
                structure.append(f'{subindent}{f}')
    return '\n'.join(structure)

def read_file_content(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as file:
            return file.read()
    except UnicodeDecodeError:
        # 如果 UTF-8 解码失败，可能是二进制文件，我们跳过它
        return "Binary file, content not shown."

def generate_markdown(root_dir):
    md_content = "# Project Structure\n\n```\n"
    md_content += generate_file_structure(root_dir)
    md_content += "\n```\n\n# File Contents\n\n"

    for root, dirs, files in os.walk(root_dir):
        dirs[:] = [d for d in dirs if not should_ignore(os.path.join(root, d))]
        for file in files:
            if should_ignore(file):
                continue
            file_path = os.path.join(root, file)
            language = get_language(file_path)
            md_content += f"## {file}\n\n```{language}\n"
            md_content += read_file_content(file_path)
            md_content += "\n```\n\n"

    return md_content

def save_markdown(content, output_file):
    with open(output_file, 'w', encoding='utf-8') as file:
        file.write(content)

# 使用示例
root_directory = "E:\\idea5\\java\\springboot3.3.x"
root_directory = "E:\\idea\\java\\springboottestNew\\springboot3.3.x\\platform-collect"
# root_directory = "E:\\idea\\java\\springboottest\\springboot3.3.x"
output_file = "project_overview.md"

md_content = generate_markdown(root_directory)
save_markdown(md_content, output_file)

print(f"Markdown file generated: {output_file}")

# 向AI提问的示例
question = "请分析这个项目的整体结构和主要功能。特别关注以下几点:\n" \
           "1. 项目的整体架构是怎样的?\n" \
           "2. 主要的类和它们的职责是什么?\n" \
           "3. 数据流是如何在不同组件之间传递的?\n" \
           "4. 有哪些可能的改进点或潜在的问题?\n" \
           "5. 项目使用了哪些主要的框架或技术栈?"

print(f"\n向AI提问:\n{question}")
print("\n请将生成的Markdown文件内容和上述问题一起发送给AI进行分析。")