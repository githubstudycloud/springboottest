import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 通用对象排序器
 * 支持自定义ID和父ID字段的排序逻辑
 */
public class GenericBeanSorter {
    
    /**
     * 创建排序器，支持自定义排序逻辑
     * @param <T> 实体类型
     * @param idExtractor 提取ID的方法引用
     * @param parentIdExtractor 提取父ID的方法引用
     * @param totalValue "total"的值
     * @param topValue "top"的值
     * @param abnormalValue "abnormal"的值 
     * @return 排序比较器
     */
    public static <T> Comparator<T> createSorter(
            Function<T, String> idExtractor,
            Function<T, String> parentIdExtractor,
            String totalValue,
            String topValue,
            String abnormalValue) {
        
        return Comparator
            // 1. parentId 等于 "total" 的放最后
            .<T>comparingInt(bean -> {
                String parentId = parentIdExtractor.apply(bean);
                return Objects.equals(parentId, totalValue) ? 1 : 0;
            })
            // 2. parentId 等于 "top" 的放倒数第二
            .thenComparingInt(bean -> {
                String parentId = parentIdExtractor.apply(bean);
                return Objects.equals(parentId, topValue) ? 1 : 0;
            })
            // 3. id 等于 "total" 的放最后
            .thenComparingInt(bean -> {
                String id = idExtractor.apply(bean);
                return Objects.equals(id, totalValue) ? 1 : 0;
            })
            // 4. 按 parentId 正序排序
            .thenComparing((bean1, bean2) -> {
                String p1 = parentIdExtractor.apply(bean1);
                String p2 = parentIdExtractor.apply(bean2);
                
                // 处理空值情况
                if (p1 == null && p2 == null) return 0;
                if (p1 == null) return -1;  // null 值排前面
                if (p2 == null) return 1;
                
                // 特殊处理 total 和 top
                if (Objects.equals(p1, totalValue) && Objects.equals(p2, totalValue)) return 0;
                if (Objects.equals(p1, totalValue)) return 1;
                if (Objects.equals(p2, totalValue)) return -1;
                
                if (Objects.equals(p1, topValue) && Objects.equals(p2, topValue)) return 0;
                if (Objects.equals(p1, topValue)) return 1;
                if (Objects.equals(p2, topValue)) return -1;
                
                // 其他情况按字符串比较
                return p1.compareTo(p2);
            })
            // 5. id 等于 "abnormal" 的放倒数第二
            .thenComparingInt(bean -> {
                String id = idExtractor.apply(bean);
                return Objects.equals(id, abnormalValue) ? 1 : 0;
            })
            // 6. 最后按 id 正序排序
            .thenComparing((bean1, bean2) -> {
                String id1 = idExtractor.apply(bean1);
                String id2 = idExtractor.apply(bean2);
                
                // 处理空值情况
                if (id1 == null && id2 == null) return 0;
                if (id1 == null) return -1;  // null 值排前面
                if (id2 == null) return 1;
                
                // 特殊处理 total 和 abnormal
                if (Objects.equals(id1, totalValue) && Objects.equals(id2, totalValue)) return 0;
                if (Objects.equals(id1, totalValue)) return 1;
                if (Objects.equals(id2, totalValue)) return -1;
                
                if (Objects.equals(id1, abnormalValue) && Objects.equals(id2, abnormalValue)) return 0;
                if (Objects.equals(id1, abnormalValue)) return 1;
                if (Objects.equals(id2, abnormalValue)) return -1;
                
                // 其他情况按字符串比较
                return id1.compareTo(id2);
            });
    }
    
    /**
     * 使用示例
     */
    public static void main(String[] args) {
        // 测试Bean类
        testWithCustomBean();
        
        // 测试其他类型
        testWithCustomClass();
    }
    
    private static void testWithCustomBean() {
        System.out.println("==== 测试Bean类 ====");
        
        // 创建测试数据
        List<Bean> beanList = new ArrayList<>();
        beanList.add(new Bean("a", "parent1"));
        beanList.add(new Bean("total", "parent2"));
        beanList.add(new Bean("c", "top"));
        beanList.add(new Bean(null, "parent3"));  // 测试null值
        beanList.add(new Bean("abnormal", null)); // 测试null值
        beanList.add(new Bean("f", "total"));
        
        // 排序前
        System.out.println("排序前:");
        beanList.forEach(System.out::println);
        
        // 创建并应用排序器
        beanList.sort(createSorter(
                Bean::getId,       // ID提取函数
                Bean::getParentId, // 父ID提取函数
                "total",           // total值
                "top",             // top值
                "abnormal"         // abnormal值
        ));
        
        // 排序后
        System.out.println("\n排序后:");
        beanList.forEach(System.out::println);
    }
    
    private static void testWithCustomClass() {
        System.out.println("\n==== 测试自定义类 ====");
        
        // 创建测试数据
        List<Product> productList = new ArrayList<>();
        productList.add(new Product("P001", "C001", "常规商品"));
        productList.add(new Product("TOTAL", "C002", "统计商品"));
        productList.add(new Product("P003", "TOP", "推荐商品"));
        productList.add(new Product("ABNORMAL", "C004", "异常商品"));
        productList.add(new Product("P005", "TOTAL", "分类统计"));
        productList.add(new Product(null, "C006", "未知商品"));  // 测试null值
        
        // 排序前
        System.out.println("排序前:");
        productList.forEach(System.out::println);
        
        // 创建并应用排序器
        productList.sort(createSorter(
                Product::getProductCode,  // ID提取函数
                Product::getCategoryCode, // 父ID提取函数
                "TOTAL",                  // total值
                "TOP",                    // top值
                "ABNORMAL"                // abnormal值
        ));
        
        // 排序后
        System.out.println("\n排序后:");
        productList.forEach(System.out::println);
    }
    
    // 原始Bean类定义
    static class Bean {
        private String id;
        private String parentId;
        
        public Bean(String id, String parentId) {
            this.id = id;
            this.parentId = parentId;
        }
        
        public String getId() {
            return id;
        }
        
        public String getParentId() {
            return parentId;
        }
        
        @Override
        public String toString() {
            return "Bean{id='" + id + "', parentId='" + parentId + "'}";
        }
    }
    
    // 示例其他类型
    static class Product {
        private String productCode;   // 商品编码，相当于id
        private String categoryCode;  // 分类编码，相当于parentId
        private String productName;   // 商品名称
        
        public Product(String productCode, String categoryCode, String productName) {
            this.productCode = productCode;
            this.categoryCode = categoryCode;
            this.productName = productName;
        }
        
        public String getProductCode() {
            return productCode;
        }
        
        public String getCategoryCode() {
            return categoryCode;
        }
        
        public String getProductName() {
            return productName;
        }
        
        @Override
        public String toString() {
            return "Product{code='" + productCode + "', category='" + categoryCode + "', name='" + productName + "'}";
        }
    }
}
