import os
from pathlib import Path
import re
import shutil
from datetime import datetime
import logging
from tqdm import tqdm
import colorama
from colorama import Fore, Style
import sys

class ProjectUpdater:
    def __init__(self, project_dir, old_package, new_package):
        self.project_dir = Path(project_dir)
        self.old_package = old_package
        self.new_package = new_package
        self.backup_dir = self.project_dir / 'backup'
        self.old_path = self.old_package.replace('.', '/')
        self.new_path = self.new_package.replace('.', '/')
        self.setup_logging()
        colorama.init()

    def setup_logging(self):
        """设置日志"""
        log_dir = self.project_dir / 'logs'
        log_dir.mkdir(exist_ok=True)
        log_file = log_dir / f'package_update_{datetime.now().strftime("%Y%m%d_%H%M%S")}.log'

        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(levelname)s - %(message)s',
            handlers=[
                logging.FileHandler(log_file),
                logging.StreamHandler(sys.stdout)
            ]
        )
        self.logger = logging.getLogger(__name__)

    def print_colored(self, text, color=Fore.WHITE, style=Style.NORMAL):
        """打印彩色文本"""
        print(f"{style}{color}{text}{Style.RESET_ALL}")

    def backup_project(self):
        """备份项目"""
        try:
            timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
            backup_path = self.backup_dir / timestamp
            self.print_colored(f"正在备份项目到: {backup_path}", Fore.CYAN)

            # 计算总文件数用于进度条
            total_files = sum(1 for _ in self.project_dir.rglob('*') if _.is_file())

            with tqdm(total=total_files, desc="备份进度") as pbar:
                def copy_with_progress(src, dst, ignore=None):
                    if ignore is None:
                        ignore = shutil.ignore_patterns('backup', '.git', 'target', 'logs')
                    os.makedirs(dst, exist_ok=True)
                    for item in os.listdir(src):
                        if ignore(src, [item]):
                            continue
                        s = os.path.join(src, item)
                        d = os.path.join(dst, item)
                        if os.path.isfile(s):
                            shutil.copy2(s, d)
                            pbar.update(1)
                        elif os.path.isdir(s):
                            copy_with_progress(s, d, ignore)

                copy_with_progress(str(self.project_dir), str(backup_path))

            self.print_colored("备份完成！", Fore.GREEN)
            return True
        except Exception as e:
            self.logger.error(f"备份失败: {str(e)}")
            return False

    def update_package_structure(self):
        """更新包结构（文件夹）"""
        try:
            src_folders = []
            for src_path in self.project_dir.rglob('src/*/java'):
                if 'main/java' in str(src_path) or 'test/java' in str(src_path):
                    src_folders.append(src_path)

            for src_folder in src_folders:
                old_package_path = src_folder / self.old_path
                new_package_path = src_folder / self.new_path

                if old_package_path.exists():
                    self.logger.info(f"处理目录: {old_package_path}")

                    # 确保新目录的父目录存在
                    new_package_path.parent.mkdir(parents=True, exist_ok=True)

                    # 如果新路径已存在，需要合并内容
                    if new_package_path.exists():
                        self.logger.info(f"目标目录已存在，合并内容: {new_package_path}")
                        for item in old_package_path.rglob('*'):
                            if item.is_file():
                                rel_path = item.relative_to(old_package_path)
                                new_file_path = new_package_path / rel_path
                                new_file_path.parent.mkdir(parents=True, exist_ok=True)
                                shutil.copy2(str(item), str(new_file_path))
                                self.logger.debug(f"复制文件: {item} -> {new_file_path}")
                    else:
                        # 创建完整的目标目录路径
                        new_package_path.parent.mkdir(parents=True, exist_ok=True)
                        self.logger.info(f"移动目录: {old_package_path} -> {new_package_path}")
                        shutil.move(str(old_package_path), str(new_package_path))

                    # 清理旧目录结构
                    self._clean_empty_dirs(old_package_path, src_folder)

            return True
        except Exception as e:
            self.logger.error(f"更新包结构时出错: {str(e)}")
            return False

    def _clean_empty_dirs(self, start_dir, stop_dir):
        """清理空目录"""
        try:
            current_dir = start_dir
            while current_dir != stop_dir and current_dir.exists():
                if not any(current_dir.iterdir()):
                    self.logger.info(f"删除空目录: {current_dir}")
                    current_dir.rmdir()
                    current_dir = current_dir.parent
                else:
                    break
        except Exception as e:
            self.logger.error(f"清理目录时出错: {str(e)}")

    def update_pom_files(self):
        """更新pom.xml文件"""
        try:
            pom_files = list(self.project_dir.rglob('pom.xml'))
            self.print_colored("\n更新POM文件...", Fore.CYAN)

            with tqdm(pom_files) as pbar:
                for pom_file in pbar:
                    pbar.set_description(f"处理 {pom_file.name}")
                    self._update_pom(pom_file)
            return True
        except Exception as e:
            self.logger.error(f"更新POM文件时出错: {str(e)}")
            return False

    def _update_pom(self, pom_file):
        """更新单个pom.xml文件"""
        try:
            with open(pom_file, 'r', encoding='utf-8') as f:
                content = f.read()

            # 保存原始内容以检查是否有更改
            original_content = content

            # 更新groupId
            patterns = [
                (f'<groupId>{self.old_package}</groupId>', f'<groupId>{self.new_package}</groupId>'),
                (f'<dependency>\\s*<groupId>{self.old_package}</groupId>',
                 f'<dependency><groupId>{self.new_package}</groupId>'),
                (f'<parent>\\s*<groupId>{self.old_package}</groupId>',
                 f'<parent><groupId>{self.new_package}</groupId>')
            ]

            for old, new in patterns:
                content = re.sub(old, new, content)

            if content != original_content:
                with open(pom_file, 'w', encoding='utf-8') as f:
                    f.write(content)
                self.logger.info(f"已更新 POM 文件: {pom_file}")

        except Exception as e:
            self.logger.error(f"更新POM文件 {pom_file} 时出错: {str(e)}")

    def update_java_files(self):
        """更新Java文件"""
        try:
            java_files = list(self.project_dir.rglob('*.java'))
            self.print_colored("\n更新Java文件...", Fore.CYAN)

            with tqdm(java_files) as pbar:
                for java_file in pbar:
                    pbar.set_description(f"处理 {java_file.name}")
                    self._update_java_file(java_file)
            return True
        except Exception as e:
            self.logger.error(f"更新Java文件时出错: {str(e)}")
            return False

    def _update_java_file(self, java_file):
        """更新单个Java文件的内容"""
        try:
            with open(java_file, 'r', encoding='utf-8') as f:
                content = f.read()

            original_content = content

            # 更新package声明
            content = re.sub(
                f'package {self.old_package}',
                f'package {self.new_package}',
                content
            )

            # 更新import语句
            content = re.sub(
                f'import {self.old_package}',
                f'import {self.new_package}',
                content
            )

            # 更新Spring Boot注解
            annotations = ['@ComponentScan', '@EntityScan', '@EnableJpaRepositories', '@MapperScan']
            for annotation in annotations:
                patterns = [
                    (f'{annotation}\\s*\\(\\s*"?{self.old_package}',
                     f'{annotation}("{self.new_package}'),
                    (f'{annotation}\\s*\\(\\s*basePackages\\s*=\\s*"?{self.old_package}',
                     f'{annotation}(basePackages = "{self.new_package}')
                ]
                for old, new in patterns:
                    content = re.sub(old, new, content)

            if content != original_content:
                with open(java_file, 'w', encoding='utf-8') as f:
                    f.write(content)
                self.logger.info(f"已更新Java文件: {java_file}")

        except Exception as e:
            self.logger.error(f"更新Java文件 {java_file} 时出错: {str(e)}")

    def update_resource_files(self):
        """更新资源文件"""
        try:
            # 更新配置文件
            config_files = list(self.project_dir.rglob('application*.yml')) + \
                           list(self.project_dir.rglob('application*.properties'))

            # 更新Mapper XML文件
            mapper_files = list(self.project_dir.rglob('mapper/**/*.xml'))

            all_files = config_files + mapper_files
            self.print_colored("\n更新资源文件...", Fore.CYAN)

            with tqdm(all_files) as pbar:
                for file in pbar:
                    pbar.set_description(f"处理 {file.name}")
                    if file.suffix == '.yml':
                        self._update_yaml_file(file)
                    elif file.suffix == '.properties':
                        self._update_properties_file(file)
                    elif file.suffix == '.xml':
                        self._update_mapper_xml(file)
            return True
        except Exception as e:
            self.logger.error(f"更新资源文件时出错: {str(e)}")
            return False

    def _update_yaml_file(self, yaml_file):
        """更新YAML配置文件"""
        try:
            with open(yaml_file, 'r', encoding='utf-8') as f:
                content = f.read()

            # 更新各种配置
            patterns = [
                (f'basePackage: {self.old_package}', f'basePackage: {self.new_package}'),
                (f'scan-packages: {self.old_package}', f'scan-packages: {self.new_package}'),
                (f'type-aliases-package: {self.old_package}', f'type-aliases-package: {self.new_package}')
            ]

            updated_content = content
            for old, new in patterns:
                updated_content = updated_content.replace(old, new)

            if updated_content != content:
                with open(yaml_file, 'w', encoding='utf-8') as f:
                    f.write(updated_content)
                self.logger.info(f"已更新配置文件: {yaml_file}")

        except Exception as e:
            self.logger.error(f"更新YAML文件 {yaml_file} 时出错: {str(e)}")

    def _update_properties_file(self, properties_file):
        """更新Properties配置文件"""
        try:
            with open(properties_file, 'r', encoding='utf-8') as f:
                content = f.read()

            updated_content = content.replace(self.old_package, self.new_package)

            if updated_content != content:
                with open(properties_file, 'w', encoding='utf-8') as f:
                    f.write(updated_content)
                self.logger.info(f"已更新配置文件: {properties_file}")

        except Exception as e:
            self.logger.error(f"更新Properties文件 {properties_file} 时出错: {str(e)}")

    def _update_mapper_xml(self, mapper_file):
        """更新Mapper XML文件"""
        try:
            with open(mapper_file, 'r', encoding='utf-8') as f:
                content = f.read()

            # 更新所有类型引用
            patterns = [
                (f'namespace="?{self.old_package}', f'namespace="{self.new_package}'),
                (f'resultType="?{self.old_package}', f'resultType="{self.new_package}'),
                (f'parameterType="?{self.old_package}', f'parameterType="{self.new_package}')
            ]

            updated_content = content
            for old, new in patterns:
                updated_content = re.sub(old, new, updated_content)

            if updated_content != content:
                with open(mapper_file, 'w', encoding='utf-8') as f:
                    f.write(updated_content)
                self.logger.info(f"已更新Mapper XML文件: {mapper_file}")

        except Exception as e:
            self.logger.error(f"更新Mapper XML文件 {mapper_file} 时出错: {str(e)}")

def main():
    try:
        # 初始化彩色输出
        colorama.init()

        # 打印欢迎信息
        print(f"{Fore.CYAN}============================================")
        print("Spring Boot 3.x 项目包名更新工具")
        print("============================================\n")
        print