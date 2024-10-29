import os
import re
import shutil
from pathlib import Path
import xml.etree.ElementTree as ET
import argparse

class MavenProjectUpdater:
    def __init__(self, project_dir):
        self.project_dir = Path(project_dir)
        self.backup_dir = self.project_dir / 'backup'

    def backup_project(self):
        """备份整个项目"""
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        backup_path = self.backup_dir / timestamp
        shutil.copytree(self.project_dir, backup_path, ignore=shutil.ignore_patterns('backup', '.git', 'target'))
        print(f"项目已备份到: {backup_path}")

    def update_pom_files(self, new_group_id=None, new_artifact_id=None, new_version=None):
        """更新所有pom.xml文件"""
        pom_files = self.project_dir.rglob('pom.xml')
        for pom_file in pom_files:
            self._update_pom_file(pom_file, new_group_id, new_artifact_id, new_version)

    def _update_pom_file(self, pom_file, new_group_id, new_artifact_id, new_version):
        """更新单个pom.xml文件"""
        try:
            # 读取文件内容
            with open(pom_file, 'r', encoding='utf-8') as f:
                content = f.read()

            # 使用正则表达式更新值
            if new_group_id:
                content = re.sub(
                    r'(<groupId>)[^<]+(</groupId>)',
                    rf'\1{new_group_id}\2',
                    content
                )

            if new_artifact_id:
                # 更新父项目的artifactId
                content = re.sub(
                    r'(<artifactId>)[^<]+(</artifactId>)',
                    rf'\1{new_artifact_id}\2',
                    content,
                    count=1  # 只替换第一次出现的
                )

                # 更新子模块的artifactId，保留原有的后缀
                original_content = content
                for match in re.finditer(r'<artifactId>(.*?)</artifactId>', original_content):
                    old_artifact_id = match.group(1)
                    if '-' in old_artifact_id:
                        suffix = old_artifact_id.split('-', 1)[1]
                        new_full_artifact_id = f"{new_artifact_id}-{suffix}"
                        content = content.replace(old_artifact_id, new_full_artifact_id)

            if new_version:
                content = re.sub(
                    r'(<version>)[^<]+(</version>)',
                    rf'\1{new_version}\2',
                    content
                )

            # 写回文件
            with open(pom_file, 'w', encoding='utf-8') as f:
                f.write(content)

            print(f"已更新 POM 文件: {pom_file}")

        except Exception as e:
            print(f"更新POM文件 {pom_file} 时出错: {str(e)}")

    def update_java_packages(self, old_group_id, new_group_id):
        """更新Java文件中的包名"""
        if not old_group_id or not new_group_id:
            return

        old_package_path = old_group_id.replace('.', '/')
        new_package_path = new_group_id.replace('.', '/')

        # 查找所有Java文件
        java_files = list(self.project_dir.rglob('*.java'))

        for java_file in java_files:
            try:
                # 读取文件内容
                with open(java_file, 'r', encoding='utf-8') as f:
                    content = f.read()

                # 更新package语句
                content = content.replace(
                    f'package {old_group_id}',
                    f'package {new_group_id}'
                )

                # 更新import语句
                content = content.replace(
                    f'import {old_group_id}',
                    f'import {new_group_id}'
                )

                # 写回文件
                with open(java_file, 'w', encoding='utf-8') as f:
                    f.write(content)

                print(f"已更新Java文件: {java_file}")

            except Exception as e:
                print(f"更新Java文件 {java_file} 时出错: {str(e)}")

        # 移动Java文件到新的包路径
        for java_file in java_files:
            if old_package_path in str(java_file):
                new_file_path = str(java_file).replace(old_package_path, new_package_path)
                new_file_path = Path(new_file_path)

                # 创建新目录
                new_file_path.parent.mkdir(parents=True, exist_ok=True)

                # 移动文件
                if str(java_file) != str(new_file_path):
                    shutil.move(str(java_file), str(new_file_path))
                    print(f"已移动文件: {java_file} -> {new_file_path}")

def main():
    parser = argparse.ArgumentParser(description='更新Maven项目的groupId、artifactId和version')
    parser.add_argument('project_dir', help='Maven项目根目录')
    parser.add_argument('--group-id', help='新的groupId')
    parser.add_argument('--artifact-id', help='新的artifactId')
    parser.add_argument('--version', help='新的version')
    parser.add_argument('--old-group-id', help='原来的groupId（用于更新Java包名）')

    args = parser.parse_args()

    if not any([args.group_id, args.artifact_id, args.version]):
        print("错误：至少需要指定一个要更新的值（groupId、artifactId 或 version）")
        return

    updater = MavenProjectUpdater(args.project_dir)

    # 备份项目
    updater.backup_project()

    # 更新POM文件
    updater.update_pom_files(args.group_id, args.artifact_id, args.version)

    # 如果提供了新旧groupId，更新Java包名
    if args.group_id and args.old_group_id:
        updater.update_java_packages(args.old_group_id, args.group_id)

if __name__ == '__main__':
    main()