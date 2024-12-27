import json
from typing import Any, Dict, List, Optional, Set, Union
import re
from collections import defaultdict

def capitalize_first_letter(s: str) -> str:
    """将字符串首字母大写,用于生成类名"""
    if not s:
        return s
    return s[0].upper() + s[1:]

def sanitize_class_name(name: str) -> str:
    """清理并规范化类名"""
    name = re.sub(r'[^a-zA-Z0-9]', '', name)
    return capitalize_first_letter(name) or "CustomClass"

def sanitize_field_name(name: str) -> str:
    """清理并规范化字段名"""
    name = re.sub(r'[^a-zA-Z0-9_]', '', name)
    if not name or name[0].isdigit():
        name = 'f_' + name
    return name

class ClassGenerator:
    def __init__(self):
        self.generated_classes: Dict[str, str] = {}
        self.class_counter: Dict[str, int] = {}
        self.field_types: Dict[str, Dict[str, Set[str]]] = defaultdict(lambda: defaultdict(set))
        self.optional_fields: Dict[str, Set[str]] = defaultdict(set)

    def get_unique_class_name(self, base_name: str) -> str:
        """生成唯一的类名"""
        base_name = sanitize_class_name(base_name)
        if base_name not in self.class_counter:
            self.class_counter[base_name] = 0
            return base_name
        
        self.class_counter[base_name] += 1
        return f"{base_name}{self.class_counter[base_name]}"

    def merge_type_hints(self, class_name: str, field_name: str) -> str:
        """合并字段的所有可能类型"""
        types = self.field_types[class_name][field_name]
        if len(types) == 1:
            type_hint = next(iter(types))
            return f"Optional[{type_hint}]" if field_name in self.optional_fields[class_name] else type_hint
        else:
            type_list = sorted(types)
            return f"Optional[Union[{', '.join(type_list)}]]" if field_name in self.optional_fields[class_name] else f"Union[{', '.join(type_list)}]"

    def get_type_hint(self, value: Any, context: str = "") -> str:
        """获取值的类型提示"""
        if value is None:
            return "Any"
        elif isinstance(value, bool):
            return "bool"
        elif isinstance(value, int):
            return "int"
        elif isinstance(value, float):
            return "float"
        elif isinstance(value, str):
            return "str"
        elif isinstance(value, list):
            if value:
                # 处理列表中的所有类型
                item_types = set()
                for item in value:
                    item_type = self.get_type_hint(item, context)
                    item_types.add(item_type)
                
                if len(item_types) == 1:
                    return f"List[{next(iter(item_types))}]"
                else:
                    return f"List[Union[{', '.join(sorted(item_types))}]]"
            return "List[Any]"
        elif isinstance(value, dict):
            return self.generate_class_from_dict(value, class_name=context)
        else:
            return "Any"

    def analyze_json_structure(self, data: Union[Dict, List], class_name: str) -> None:
        """分析JSON结构，收集字段信息"""
        if isinstance(data, list):
            for item in data:
                if isinstance(item, dict):
                    self.analyze_json_structure(item, class_name)
        elif isinstance(data, dict):
            for key, value in data.items():
                field_name = sanitize_field_name(key)
                if value is None:
                    self.optional_fields[class_name].add(field_name)
                type_hint = self.get_type_hint(value, f"{class_name}{capitalize_first_letter(key)}")
                self.field_types[class_name][field_name].add(type_hint)

    def generate_class_from_dict(self, data: Dict[str, Any], class_name: str = "Root") -> str:
        """从字典生成类定义"""
        class_name = self.get_unique_class_name(class_name)
        self.analyze_json_structure(data, class_name)
        
        if class_name in self.generated_classes:
            return class_name

        fields: List[str] = []
        init_params: List[str] = []
        init_assignments: List[str] = []
        
        for field_name in self.field_types[class_name]:
            type_hint = self.merge_type_hints(class_name, field_name)
            default_value = " = None" if field_name in self.optional_fields[class_name] else ""
            
            fields.append(f"    {field_name}: {type_hint}")
            init_params.append(f"{field_name}: {type_hint}{default_value}")
            init_assignments.append(f"        self.{field_name} = {field_name}")

        class_def = [
            f"class {class_name}:",
            "    def __init__(self, " + ", ".join(init_params) + "):",
            *init_assignments,
            "",
            "    @classmethod",
            "    def from_dict(cls, data: Dict[str, Any]) -> '" + class_name + "':",
            "        return cls(**{",
            *[f"            '{field}': data.get('{field}')," for field in self.field_types[class_name]],
            "        })",
            "",
            "    def to_dict(self) -> Dict[str, Any]:",
            "        return {",
            *[f"            '{field}': self.{field}," for field in self.field_types[class_name]],
            "        }",
            "",
            "    def merge(self, other: '" + class_name + "') -> None:",
            "        \"\"\"合并另一个对象的非None字段\"\"\"",
            "        for field in self.__annotations__:",
            "            other_value = getattr(other, field, None)",
            "            if other_value is not None:",
            "                setattr(self, field, other_value)"
        ]

        self.generated_classes[class_name] = "\n".join(class_def)
        return class_name

    def generate_code(self, json_input: Union[str, List[str]], root_class_name: str = "Root") -> str:
        """生成完整的Python代码"""
        try:
            if isinstance(json_input, str):
                json_input = [json_input]
            
            # 解析所有JSON输入
            all_data = []
            for json_str in json_input:
                data = json.loads(json_str)
                if isinstance(data, list):
                    all_data.extend(data)
                else:
                    all_data.append(data)
            
            # 分析所有数据结构
            for data in all_data:
                if isinstance(data, dict):
                    self.generate_class_from_dict(data, root_class_name)
            
            # 组合所有生成的类
            imports = [
                "from typing import Any, Dict, List, Optional, Union",
                "import json",
                ""
            ]
            
            classes = list(self.generated_classes.values())
            
            return "\n".join(imports + classes)
            
        except json.JSONDecodeError as e:
            raise ValueError(f"Invalid JSON string: {str(e)}")

    def update_from_new_json(self, existing_code: str, new_json_str: str, root_class_name: str = "Root") -> str:
        """根据新的JSON更新现有代码"""
        # 保存现有的类信息
        self.generated_classes = {}
        self.class_counter = {}
        self.field_types = defaultdict(lambda: defaultdict(set))
        self.optional_fields = defaultdict(set)
        
        # 解析现有代码中的类型信息
        exec(existing_code, globals())
        
        # 解析新的JSON并更新类型信息
        return self.generate_code(new_json_str, root_class_name)

# 使用示例
if __name__ == "__main__":
    # 示例1: 处理包含空值的JSON
    json_str1 = '''
    {
        "name": "John Doe",
        "age": 30,
        "email": null,
        "address": {
            "street": "123 Main St",
            "city": null,
            "country": "USA"
        }
    }
    '''
    
    # 示例2: 处理列表中的多个JSON
    json_str2 = '''
    [
        {
            "name": "Jane Smith",
            "age": 25,
            "email": "jane@example.com",
            "address": {
                "street": "456 Oak St",
                "city": "Boston",
                "country": "USA"
            }
        },
        {
            "name": "Bob Johnson",
            "age": null,
            "email": "bob@example.com",
            "address": {
                "street": "789 Pine St",
                "city": "Chicago",
                "zipcode": "60601"
            }
        }
    ]
    '''
    
    # 示例3: 新字段的JSON
    json_str3 = '''
    {
        "name": "Alice Brown",
        "age": 28,
        "phone": "+1234567890",
        "address": {
            "street": "321 Elm St",
            "city": "Seattle",
            "country": "USA",
            "coordinates": {
                "latitude": 47.6062,
                "longitude": -122.3321
            }
        }
    }
    '''
    
    # 生成初始代码
    generator = ClassGenerator()
    print("=== 初始代码生成 ===")
    initial_code = generator.generate_code([json_str1, json_str2], "Person")
    print(initial_code)
    
    # 使用新JSON更新代码
    print("\n=== 更新后的代码 ===")
    updated_code = generator.update_from_new_json(initial_code, json_str3, "Person")
    print(updated_code)
    
    # 测试生成的代码
    print("\n=== 测试代码功能 ===")
    exec(updated_code)
    
    # 测试数据转换
    data1 = json.loads(json_str1)
    data2 = json.loads(json_str2)[0]
    data3 = json.loads(json_str3)
    
    # 创建实例并测试合并功能
    person1 = eval("Person.from_dict(data1)")
    person2 = eval("Person.from_dict(data2)")
    person3 = eval("Person.from_dict(data3)")
    
    print("\n原始数据:")
    print(f"Person 1: {person1.to_dict()}")
    print(f"Person 2: {person2.to_dict()}")
    print(f"Person 3: {person3.to_dict()}")
    
    # 测试合并功能
    person1.merge(person2)
    print("\n合并后数据:")
    print(json.dumps(person1.to_dict(), indent=2, ensure_ascii=False))