import json
import os
from typing import Dict, Set, List
import difflib

class JavaEntityGenerator:
    def __init__(self):
        self.type_mapping = {
            'str': 'String',
            'int': 'Integer',
            'float': 'Double',
            'bool': 'Boolean',
            'list': 'List',
            'dict': 'Map'
        }
        self.existing_entities = {}  # Store existing entity definitions
        self.changes_log = []  # Track changes between versions

    def _get_java_type(self, value):
        python_type = type(value).__name__
        if python_type == 'list':
            if value:  # If list is not empty, check first element type
                element_type = self._get_java_type(value[0])
                return f"List<{element_type}>"
            return "List<Object>"
        elif python_type == 'dict':
            return self._generate_nested_class_name(list(value.keys())[0])
        return self.type_mapping.get(python_type, 'Object')

    def _generate_nested_class_name(self, prefix):
        return f"{prefix.capitalize()}Data"

    def _format_field_name(self, name: str) -> str:
        # Convert snake_case to camelCase
        components = name.split('_')
        return components[0] + ''.join(x.title() for x in components[1:])

    def generate_entity(self, json_input: str, class_name: str, output_dir: str = "generated", package_path: str = None) -> str:
        """
        Generate Java entity classes from JSON input
        :param json_input: JSON string or path to JSON file
        :param class_name: Name of the main Java class
        :param output_dir: Output directory for generated files
        :param package_path: Java package path (e.g., 'com.example.model')
        """
        try:
            # Check if input is a file path
            if os.path.isfile(json_input):
                with open(json_input, 'r', encoding='utf-8') as f:
                    data = json.load(f)
            else:
                data = json.loads(json_input)
        except json.JSONDecodeError:
            raise ValueError("Invalid JSON input")
        except FileNotFoundError:
            raise ValueError(f"JSON file not found: {json_input}")

        # Create output directory structure based on package path
        if package_path:
            package_dir = os.path.join(output_dir, *package_path.split('.'))
            os.makedirs(package_dir, exist_ok=True)
        else:
            package_dir = output_dir
            os.makedirs(package_dir, exist_ok=True)

        # Store previous version if exists
        if class_name in self.existing_entities:
            self.changes_log.append(f"Updating entity: {class_name}")

        # Generate main class and nested classes
        main_code, nested_classes = self._generate_java_class(data, class_name, package_path)
        
        # Compare with existing entity if any
        if class_name in self.existing_entities:
            old_code = self.existing_entities[class_name]
            self._track_changes(old_code, main_code, class_name)

        # Save new version of main class
        self.existing_entities[class_name] = main_code

        # Write main class file
        main_file_path = os.path.join(package_dir, f"{class_name}.java")
        with open(main_file_path, 'w', encoding='utf-8') as f:
            f.write(main_code)

        # Write nested class files
        for nested_name, nested_code in nested_classes:
            nested_file_path = os.path.join(package_dir, f"{nested_name}.java")
            with open(nested_file_path, 'w', encoding='utf-8') as f:
                f.write(nested_code)

        # Generate changelog
        self._generate_changelog(output_dir)

        return main_code

    def _generate_java_class(self, data: Dict, class_name: str, package_path: str = None) -> tuple:
        """
        Generate Java class with support for separate nested class files
        Returns: (main_class_code, list of (nested_class_name, nested_class_code))
        """
        fields = []
        nested_classes = []
        imports = {
            'import lombok.Data;',
            'import com.fasterxml.jackson.annotation.JsonProperty;'
        }
        
        if package_path:
            imports.add(f'package {package_path};')

        for key, value in data.items():
            java_type = self._get_java_type(value)
            if java_type.startswith('List'):
                imports.add('import java.util.List;')
            
            field_name = self._format_field_name(key)
            
            # Generate nested class if needed
            if isinstance(value, dict):
                nested_class_name = self._generate_nested_class_name(key)
                if package_path:
                    imports.add(f'import {package_path}.{nested_class_name};')
                nested_class_code, sub_nested = self._generate_java_class(value, nested_class_name, package_path)
                nested_classes.append((nested_class_name, nested_class_code))
                nested_classes.extend(sub_nested)
                java_type = nested_class_name

            # Add field with JsonProperty annotation
            fields.append(f'    @JsonProperty("{key}")\n    private {java_type} {field_name};')

        # Build class code
        code = '\n'.join(sorted(imports)) + '\n\n'
        code += '@Data\n'
        code += f'public class {class_name} {{\n'
        code += '\n\n'.join(fields)
        code += '\n}\n'

        return code, nested_classes

    def _track_changes(self, old_code: str, new_code: str, class_name: str):
        # Compare old and new code to detect changes
        old_lines = old_code.splitlines()
        new_lines = new_code.splitlines()
        
        diff = difflib.unified_diff(old_lines, new_lines, lineterm='')
        
        added_fields = []
        removed_fields = []
        modified_fields = []
        
        for line in diff:
            if line.startswith('+') and 'private' in line:
                added_fields.append(line[1:].strip())
            elif line.startswith('-') and 'private' in line:
                removed_fields.append(line[1:].strip())

        # Log changes
        if added_fields:
            self.changes_log.append(f"\nAdded fields in {class_name}:")
            self.changes_log.extend([f"  + {field}" for field in added_fields])
        
        if removed_fields:
            self.changes_log.append(f"\nRemoved fields in {class_name}:")
            self.changes_log.extend([f"  - {field}" for field in removed_fields])

    def _generate_changelog(self, output_dir: str):
        changelog_path = os.path.join(output_dir, "CHANGELOG.md")
        with open(changelog_path, 'w') as f:
            f.write("# Entity Changes Log\n\n")
            f.write("## Latest Changes\n")
            f.write('\n'.join(self.changes_log))

# Enhanced usage example
def main():
    import argparse
    
    parser = argparse.ArgumentParser(description='Generate Java entities from JSON')
    parser.add_argument('--input', '-i', required=True, help='Input JSON file path or JSON string')
    parser.add_argument('--class-name', '-c', required=True, help='Main class name')
    parser.add_argument('--package', '-p', help='Java package path (e.g., com.example.model)')
    parser.add_argument('--output-dir', '-o', default='generated', help='Output directory for generated files')
    
    args = parser.parse_args()
    
    generator = JavaEntityGenerator()
    
    try:
        # Generate entity classes
        generator.generate_entity(
            json_input=args.input,
            class_name=args.class_name,
            output_dir=args.output_dir,
            package_path=args.package
        )
        print(f"Successfully generated Java entities in {args.output_dir}")
        print("Check CHANGELOG.md for details about any updates")
        
    except Exception as e:
        print(f"Error: {str(e)}")
        return 1
    
    return 0

if __name__ == "__main__":
    main()
