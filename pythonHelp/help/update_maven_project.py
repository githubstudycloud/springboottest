import os
from pathlib import Path
import re
import shutil
from datetime import datetime
import yaml

class ProjectUpdater:
    def __init__(self, project_dir, old_package, new_package):
        self.project_dir = Path(project_dir)
        self.old_package = old_package
        self.new_package = new_package
        self.backup_dir = self.project_dir / 'backup'
        self.old_path = self.old_package.replace('.', '/')
        self.new_path = self.new_package.replace('.', '/')

    def backup_project(self):
        """备份项目"""
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        backup_path = self.backup_dir / timestamp
        shutil.copytree(self.project_dir, backup_path, ignore=shutil.ignore_patterns('backup', '.git', 'target'))
        print(f"项目已备份到: {backup_path}")

def update_package_structure(self):
    """更新包结构（文件夹）"""
    try:
        # 查找所有src/main/java和src/test/java目录
        src_folders = []
        for src_path in self.project_dir.rglob('src/*/java'):
            if 'main/java' in str(src_path) or 'test/java' in str(src_path):
                src_folders.append(src_path)

        for src_folder in src_folders:
            old_package_path = src_folder / self.old_path
            new_package_path = src_folder / self.new_path

            if old_package_path.exists():
                print(f"处理目录: {old_package_path}")

                # 确保新目录的父目录存在
                new_package_path.parent.mkdir(parents=True, exist_ok=True)

                # 如果新路径已存在，需要合并内容
                if new_package_path.exists():
                    print(f"目标目录已存在，合并内容: {new_package_path}")
                    # 复制所有文件
                    for item in old_package_path.rglob('*'):
                        if item.is_file():
                            # 计算相对路径
                            rel_path = item.relative_to(old_package_path)
                            new_file_path = new_package_path / rel_path
                            # 确保目标文件的父目录存在
                            new_file_path.parent.mkdir(parents=True, exist_ok=True)
                            # 复制文件
                            shutil.copy2(str(item), str(new_file_path))
                            print(f"复制文件: {item} -> {new_file_path}")
                else:
                    # 创建完整的目标目录路径
                    new_package_path.parent.mkdir(parents=True, exist_ok=True)
                    # 直接移动整个目录
                    print(f"移动目录: {old_package_path} -> {new_package_path}")
                    shutil.move(str(old_package_path), str(new_package_path))

                # 清理旧目录结构
                try:
                    # 从最深层目录开始，逐级向上删除空目录
                    current_dir = old_package_path
                    while current_dir != src_folder:
                        if current_dir.exists() and not any(current_dir.iterdir()):
                            print(f"删除空目录: {current_dir}")
                            current_dir.rmdir()
                            current_dir = current_dir.parent
                        else:
                            break
                except Exception as e:
                    print(f"清理目录时出错: {str(e)}")

    except Exception as e:
        print(f"更新包结构时出错: {str(e)}")

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
        for java_file in self.project_dir.rglob('*.java'):
            self._update_java_file(java_file)

    def _update_java_file(self, java_file):
        """更新单个Java文件的内容"""
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

            # 更新Spring Boot相关注解中的包名
            annotations = ['@ComponentScan', '@EntityScan', '@EnableJpaRepositories', '@MapperScan']
            for annotation in annotations:
                updated_content = re.sub(
                    f'{annotation}\\s*\\(\\s*"?{self.old_package}',
                    f'{annotation}("{self.new_package}',
                    updated_content
                )
                updated_content = re.sub(
                    f'{annotation}\\s*\\(\\s*basePackages\\s*=\\s*"?{self.old_package}',
                    f'{annotation}(basePackages = "{self.new_package}',
                    updated_content
                )

            if content != updated_content:
                with open(java_file, 'w', encoding='utf-8') as f:
                    f.write(updated_content)
                print(f"已更新Java文件内容: {java_file}")

        except Exception as e:
            print(f"处理文件 {java_file} 时出错: {str(e)}")

    def update_resource_files(self):
        """更新资源文件中的包名引用"""
        # 更新 application.yml/application.properties
        for config_file in self.project_dir.rglob('application*.yml'):
            self._update_yaml_file(config_file)

        for config_file in self.project_dir.rglob('application*.properties'):
            self._update_properties_file(config_file)

        # 更新 mapper xml 文件
        for mapper_file in self.project_dir.rglob('mapper/**/*.xml'):
            self._update_mapper_xml(mapper_file)

    def _update_yaml_file(self, yaml_file):
        """更新YAML配置文件"""
        try:
            with open(yaml_file, 'r', encoding='utf-8') as f:
                content = f.read()

            # 更新mybatis扫描路径
            updated_content = re.sub(
                f'basePackage: {self.old_package}',
                f'basePackage: {self.new_package}',
                content
            )

            # 更新Spring包扫描路径
            updated_content = re.sub(
                f'scan-packages: {self.old_package}',
                f'scan-packages: {self.new_package}',
                updated_content
            )

            if content != updated_content:
                with open(yaml_file, 'w', encoding='utf-8') as f:
                    f.write(updated_content)
                print(f"已更新配置文件: {yaml_file}")

        except Exception as e:
            print(f"处理配置文件 {yaml_file} 时出错: {str(e)}")

    def _update_properties_file(self, properties_file):
        """更新Properties配置文件"""
        try:
            with open(properties_file, 'r', encoding='utf-8') as f:
                content = f.read()

            # 更新各种包路径配置
            updated_content = content.replace(self.old_package, self.new_package)

            if content != updated_content:
                with open(properties_file, 'w', encoding='utf-8') as f:
                    f.write(updated_content)
                print(f"已更新配置文件: {properties_file}")

        except Exception as e:
            print(f"处理配置文件 {properties_file} 时出错: {str(e)}")

    def _update_mapper_xml(self, mapper_file):
        """更新Mapper XML文件"""
        try:
            with open(mapper_file, 'r', encoding='utf-8') as f:
                content = f.read()

            # 更新namespace
            updated_content = re.sub(
                f'namespace="?{self.old_package}',
                f'namespace="{self.new_package}',
                content
            )

            # 更新resultType
            updated_content = re.sub(
                f'resultType="?{self.old_package}',
                f'resultType="{self.new_package}',
                updated_content
            )

            # 更新parameterType
            updated_content = re.sub(
                f'parameterType="?{self.old_package}',
                f'parameterType="{self.new_package}',
                updated_content
            )

            if content != updated_content:
                with open(mapper_file, 'w', encoding='utf-8') as f:
                    f.write(updated_content)
                print(f"已更新Mapper XML文件: {mapper_file}")

        except Exception as e:
            print(f"处理Mapper XML文件 {mapper_file} 时出错: {str(e)}")

def main():
    print("Spring Boot 3.x 项目包名更新工具")
    print("=" * 50)

    # 配置项目路径和包名
    project_dir = input("请输入项目根目录路径: ")
    old_package = input("请输入原包名 (默认: com.study): ") or "com.study"
    new_package = input("请输入新包名 (例如: com.test.yy): ")

    if not new_package:
        print("错误：新包名不能为空")
        return

    updater = ProjectUpdater(project_dir, old_package, new_package)

    print("\n开始更新...")

    # 备份项目
    print("\n1. 创建项目备份...")
    updater.backup_project()

    # 先更新包结构
    print("\n2. 更新包结构...")
    updater.update_package_structure()

    # 然后更新文件内容
    print("\n3. 更新POM文件...")
    updater.update_pom_files()

    print("\n4. 更新Java文件内容...")
    updater.update_java_files()

    print("\n5. 更新资源文件...")
    updater.update_resource_files()

    print("\n更新完成！请检查以下内容：")
    print("1. 检查项目是否能正常编译")
    print("2. 检查包扫描配置是否正确")
    print("3. 检查Mapper接口和XML文件的对应关系")
    print("4. 检查数据库实体类的包名映射")
    print("5. 检查测试类的包名和引用")
    print("6. 检查Spring Boot启动类的包名")
    print("7. 检查配置文件中的包名引用")

if __name__ == "__main__":
    main()