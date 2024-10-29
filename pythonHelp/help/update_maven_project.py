import os
from pathlib import Path
import re
import shutil
from datetime import datetime

class ProjectUpdater:
    def __init__(self, project_dir, old_package, new_package):
        self.project_dir = Path(project_dir)
        self.old_package = old_package
        self.new_package = new_package
        self.backup_dir = self.project_dir / 'backup'

    def backup_project(self):
        """备份项目"""
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        backup_path = self.backup_dir / timestamp
        shutil.copytree(self.project_dir, backup_path, ignore=shutil.ignore_patterns('backup', '.git', 'target'))
        print(f"项目已备份到: {backup_path}")

    def update_pom_files(self):
        """更新pom.xml文件中的包名"""
        for pom_file in self.project_dir.rglob('pom.xml'):
            self._update_pom(pom_file)

    def _update_pom(self, pom_file):
        """更新单个pom.xml文件"""
        with open(pom_file, 'r', encoding='utf-8') as f:
            content = f.read()

        # 更新项目本身的groupId
        updated_content = re.sub(
            f'<groupId>{self.old_package}</groupId>',
            f'<groupId>{self.new_package}</groupId>',
            content
        )

        # 更新对其他模块的依赖
        updated_content = re.sub(
            f'<dependency>\\s*<groupId>{self.old_package}</groupId>',
            f'<dependency><groupId>{self.new_package}</groupId>',
            updated_content
        )

        # 更新parent的groupId
        updated_content = re.sub(
            f'<parent>\\s*<groupId>{self.old_package}</groupId>',
            f'<parent><groupId>{self.new_package}</groupId>',
            updated_content
        )

        if content != updated_content:
            with open(pom_file, 'w', encoding='utf-8') as f:
                f.write(updated_content)
            print(f"已更新 POM 文件: {pom_file}")

    def update_java_files(self):
        """更新Java文件中的包名"""
        old_path = self.old_package.replace('.', '/')
        new_path = self.new_package.replace('.', '/')

        for java_file in self.project_dir.rglob('*.java'):
            self._update_java_file(java_file, old_path, new_path)

    def _update_java_file(self, java_file, old_path, new_path):
        """更新单个Java文件"""
        try:
            with open(java_file, 'r', encoding='utf-8') as f:
                content = f.read()

            # 更新package声明
            updated_content = re.sub(
                f'package {self.old_package}',
                f'package {self.new_package}',
                content
            )

            # 更新import语句
            updated_content = re.sub(
                f'import {self.old_package}',
                f'import {self.new_package}',
                updated_content
            )

            if content != updated_content:
                with open(java_file, 'w', encoding='utf-8') as f:
                    f.write(updated_content)
                print(f"已更新Java文件: {java_file}")

            # 移动文件到新的包路径
            if old_path in str(java_file):
                new_file_path = str(java_file).replace(old_path, new_path)
                new_dir = os.path.dirname(new_file_path)

                if not os.path.exists(new_dir):
                    os.makedirs(new_dir)

                if str(java_file) != new_file_path:
                    shutil.move(str(java_file), new_file_path)
                    print(f"已移动文件: {java_file} -> {new_file_path}")

        except Exception as e:
            print(f"处理文件 {java_file} 时出错: {str(e)}")

def main():
    # 配置项目路径和包名
    project_dir = input("请输入项目根目录路径: ")
    old_package = input("请输入原包名 (默认: com.study): ") or "com.study"
    new_package = input("请输入新包名 (例如: com.test.yy): ")

    if not new_package:
        print("错误：新包名不能为空")
        return

    updater = ProjectUpdater(project_dir, old_package, new_package)

    # 备份项目
    updater.backup_project()

    # 更新文件
    updater.update_pom_files()
    updater.update_java_files()

    print("\n更新完成！请检查更新结果。")

if __name__ == "__main__":
    main()