import os
import shutil
from pathlib import Path
import re
from datetime import datetime
from typing import List, Set

class PackageUpdater:
    def __init__(self, project_dir: str, old_package: str, new_package: str):
        self.project_dir = Path(project_dir)
        self.old_package = old_package
        self.new_package = new_package
        self.old_path = old_package.replace('.', '/')
        self.new_path = new_package.replace('.', '/')
        self.backup_dir = self.project_dir / 'backup'
        self.source_dirs: Set[Path] = set()
        self._find_source_dirs()

    def _find_source_dirs(self):
        """查找所有Java源码目录"""
        # 查找所有src/main/java和src/test/java目录
        for src_dir in self.project_dir.rglob('src/*/java'):
            if 'target' not in str(src_dir):  # 排除target目录
                self.source_dirs.add(src_dir)
                print(f"找到源码目录: {src_dir}")

    def backup_project(self):
        """备份项目"""
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        backup_path = self.backup_dir / timestamp
        print(f"正在备份项目到: {backup_path}")
        shutil.copytree(
            self.project_dir,
            backup_path,
            ignore=shutil.ignore_patterns('backup', 'target', '.git', '*.pyc', '__pycache__', '.idea')
        )
        print("备份完成")
        return backup_path

    def update_package_directories(self):
        """更新包目录结构"""
        print(f"\n开始更新目录结构...")
        print(f"将 {self.old_path} 改为 {self.new_path}")

        for src_dir in self.source_dirs:
            old_pkg_dir = src_dir / self.old_path
            new_pkg_dir = src_dir / self.new_path

            if old_pkg_dir.exists():
                print(f"\n处理目录: {old_pkg_dir}")

                # 确保新目录的父目录存在
                new_pkg_dir.parent.mkdir(parents=True, exist_ok=True)

                try:
                    if new_pkg_dir.exists():
                        print(f"目标目录已存在，合并内容: {new_pkg_dir}")
                        # 如果目标目录已存在，复制文件
                        for item in old_pkg_dir.rglob('*'):
                            if item.is_file():
                                rel_path = item.relative_to(old_pkg_dir)
                                new_file_path = new_pkg_dir / rel_path
                                new_file_path.parent.mkdir(parents=True, exist_ok=True)
                                shutil.copy2(item, new_file_path)
                                print(f"复制文件: {item} -> {new_file_path}")
                    else:
                        # 如果目标目录不存在，直接移动整个目录
                        print(f"移动目录: {old_pkg_dir} -> {new_pkg_dir}")
                        # 确保父目录存在
                        new_pkg_dir.parent.mkdir(parents=True, exist_ok=True)
                        shutil.move(str(old_pkg_dir), str(new_pkg_dir))

                    # 清理空目录
                    self._clean_empty_dirs(old_pkg_dir.parent, src_dir)

                except Exception as e:
                    print(f"处理目录时出错: {str(e)}")

    def _clean_empty_dirs(self, start_dir: Path, stop_dir: Path):
        """递归删除空目录"""
        if not start_dir.exists() or start_dir == stop_dir:
            return

        try:
            # 从下往上递归删除空目录
            for dirpath, dirnames, filenames in os.walk(str(start_dir), topdown=False):
                current_dir = Path(dirpath)
                if not any(current_dir.iterdir()):  # 如果目录为空
                    print(f"删除空目录: {current_dir}")
                    current_dir.rmdir()
        except Exception as e:
            print(f"清理目录时出错: {str(e)}")

    def update_file_contents(self):
        """更新文件内容中的包名引用"""
        print("\n开始更新文件内容...")

        # 更新所有Java文件
        for src_dir in self.source_dirs:
            for java_file in src_dir.rglob('*.java'):
                self._update_java_file(java_file)

        # 更新所有pom.xml文件
        for pom_file in self.project_dir.rglob('pom.xml'):
            self._update_pom_file(pom_file)

    def _update_java_file(self, file_path: Path):
        """更新Java文件内容"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()

            # 更新package语句
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
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(updated_content)
                print(f"已更新文件: {file_path}")

        except Exception as e:
            print(f"更新文件失败 {file_path}: {str(e)}")

    def _update_pom_file(self, file_path: Path):
        """更新pom.xml文件内容"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()

            # 更新groupId
            updated_content = re.sub(
                f'<groupId>{self.old_package}</groupId>',
                f'<groupId>{self.new_package}</groupId>',
                content
            )

            if content != updated_content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(updated_content)
                print(f"已更新POM文件: {file_path}")

        except Exception as e:
            print(f"更新POM文件失败 {file_path}: {str(e)}")

def main():
    print("Maven项目包名更新工具")
    print("=" * 50)

    # 获取用户输入
    project_dir = input("请输入项目根目录路径: ").strip()
    old_package = input("请输入原包名 (默认: com.study): ").strip() or "com.study"
    new_package = input("请输入新包名 (例如: com.test.yy): ").strip()

    if not new_package:
        print("错误：新包名不能为空")
        return

    if not os.path.exists(project_dir):
        print("错误：项目目录不存在")
        return

    try:
        updater = PackageUpdater(project_dir, old_package, new_package)

        # 1. 备份项目
        print("\n第1步: 备份项目")
        backup_path = updater.backup_project()
        print(f"项目已备份到: {backup_path}")

        # 2. 更新目录结构
        print("\n第2步: 更新目录结构")
        updater.update_package_directories()

        # 3. 更新文件内容
        print("\n第3步: 更新文件内容")
        updater.update_file_contents()

        print("\n更新完成！请检查以下内容：")
        print("1. 检查目录结构是否正确")
        print("2. 检查Java文件的package声明")
        print("3. 检查import语句")
        print("4. 检查pom.xml中的groupId")
        print("5. 尝试编译项目验证更新是否成功")
        print(f"\n如果需要回滚，备份文件位于: {backup_path}")

    except Exception as e:
        print(f"\n更新过程中出错: {str(e)}")
        print("请从备份恢复项目并重试")

if __name__ == "__main__":
    main()