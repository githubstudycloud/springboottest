## Prompt:
springboot2.7.18怎么写一个类，写一个方法，调试中执行后，调试后面的代码可以打印带参数用于bug查找，在idea评估表达式中写代码的时候

## Response:
我来帮你创建一个带有调试功能的 SpringBoot 类示例。

```java
@Service
public class DebugDemoService {
    
    private static final Logger log = LoggerFactory.getLogger(DebugDemoService.class);

    public void processData(String input, Integer count) {
        // 在这里设置断点
        String result = doSomething(input, count);
        
        // 调试时,在 IDEA Evaluate Expression(Alt + F8) 中可以输入以下代码:
        // String debug = String.format("input=%s, count=%d, result=%s", input, count, result);
        // System.out.println(debug);
        
        // 或者使用日志
        // log.debug("Debug info: input={}, count={}, result={}", input, count, result);
        
        // 也可以在 Evaluate Expression 中使用 Stream 来检查集合
        // list.stream().filter(item -> item.contains("test")).collect(Collectors.toList())
        
        // 继续处理逻辑
        if (result != null) {
            // 做其他操作
        }
    }

    private String doSomething(String input, Integer count) {
        // 示例业务逻辑
        if (count <= 0) {
            return null;
        }
        return input.repeat(count);
    }
}
```

使用说明：

1.  在方法中设置断点，运行到断点处
2.  使用 IDEA 的 Evaluate Expression（快捷键 Alt + F8）
3.  在评估窗口中可以输入以下类型的代码进行调试：

```java
// 1. 简单打印变量值
System.out.println("input = " + input);

// 2. 格式化打印多个变量
String debug = String.format("input=%s, count=%d", input, count);
System.out.println(debug);

// 3. 使用日志对象
log.debug("Debug info: {}, {}", input, count);

// 4. 对集合进行操作和检查
List<String> list = Arrays.asList("test1", "test2", "test3");
list.stream()
    .filter(item -> item.contains("test"))
    .forEach(System.out::println);

// 5. 执行一些简单的表达式
boolean isValid = input != null && count > 0;
System.out.println("isValid: " + isValid);
```

调试建议：

1.  在关键的业务逻辑点设置断点
2.  使用 log.debug() 而不是 System.out.println()，这样可以通过配置文件控制日志级别
3.  在评估表达式窗口中，可以执行任何有效的 Java 代码
4.  对于复杂对象，可以使用 JSON 工具类转换后打印：

```java
// 在 Evaluate Expression 中
ObjectMapper mapper = new ObjectMapper();
System.out.println(mapper.writeValueAsString(complexObject));
```

## Prompt:
我需要监听的是mybatissql执行，打印完整的执行的sql

## Response:
我来介绍几种在 SpringBoot 2.7.18 中打印完整 MyBatis SQL 的方法：

1.  最简单的方法是通过配置文件：

```yaml
# application.yml
mybatis:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

2.  如果想更细粒度控制，可以通过 logback 配置：

```xml
<!-- logback-spring.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 其他配置 -->
    
    <!-- MyBatis SQL 日志 -->
    <logger name="com.your.package.mapper" level="DEBUG"/>
    
    <!-- 如果想看完整的 SQL 参数绑定 -->
    <logger name="org.apache.ibatis.logging" level="DEBUG"/>
</configuration>
```

3.  如果需要在调试时动态查看，可以创建一个拦截器：

```java
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
@Component
public class SqlDebugInterceptor implements Interceptor {
    
    private static final Logger log = LoggerFactory.getLogger(SqlDebugInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        
        // 获取完整 SQL
        String sql = boundSql.getSql();
        // 获取参数
        Object parameterObject = boundSql.getParameterObject();
        
        // 在调试时可以在这里设置断点，然后在 Evaluate Expression 中执行：
        // System.out.println("SQL: " + sql);
        // System.out.println("Parameters: " + JSONObject.toJSONString(parameterObject));
        
        log.debug("Full SQL: {}", sql);
        log.debug("Parameters: {}", parameterObject);
        
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
```

4.  然后在配置类中注册这个拦截器：

```java
@Configuration
public class MyBatisConfig {
    
    @Bean
    public SqlDebugInterceptor sqlDebugInterceptor() {
        return new SqlDebugInterceptor();
    }
    
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            configuration.addInterceptor(sqlDebugInterceptor());
        };
    }
}
```

在调试时的使用技巧：

1.  在拦截器的 intercept 方法中设置断点
2.  在 IDEA 的 Evaluate Expression (Alt + F8) 中可以执行：

```java
// 打印原始 SQL
System.out.println("Original SQL: " + boundSql.getSql());

// 打印带参数的 SQL
Configuration configuration = mappedStatement.getConfiguration();
Object parameterObject = boundSql.getParameterObject();
MetaObject metaObject = configuration.newMetaObject(parameterObject);

// 获取参数映射
List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
String sql = boundSql.getSql();
for (ParameterMapping parameterMapping : parameterMappings) {
    String property = parameterMapping.getProperty();
    Object value = metaObject.getValue(property);
    sql = sql.replaceFirst("\\?", String.valueOf(value));
}
System.out.println("Complete SQL: " + sql);
```

这些方法各有优势：

*   配置文件方式最简单，但不够灵活
*   logback 配置可以细粒度控制日志级别
*   拦截器方式最灵活，可以在调试时动态查看和修改

建议：

1.  开发环境使用 DEBUG 级别，方便调试
2.  生产环境建议使用 INFO 或更高级别，避免性能影响
3.  如果 SQL 较多，可以只针对特定包或类开启日志
4.  对于敏感数据，注意日志脱敏处理

## Prompt:
我需要打印完整的执行sql带参数，可以直接在sql命令行运行，是个静态工具类，我需要在评估表达式中调用用于调试

## Response:
我来写一个静态工具类，可以在调试时直接在 IDEA 的 Evaluate Expression 中调用来获取完整的 SQL：

```java
public class SqlDebugUtil {
    
    /**
     * 获取完整的可执行 SQL
     * @param boundSql MyBatis的BoundSql对象
     * @param configuration MyBatis的Configuration对象
     * @return 可直接执行的SQL语句
     */
    public static String getFullSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql();
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        
        if (parameterMappings != null && !parameterMappings.isEmpty()) {
            sql = sql.replaceAll("\\n", " ").replaceAll("\\s+", " ");
            
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                Object value;
                
                if (boundSql.hasAdditionalParameter(propertyName)) {
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (parameterObject == null) {
                    value = null;
                } else if (configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                    value = parameterObject;
                } else {
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    value = metaObject.getValue(propertyName);
                }
                
                if (value == null) {
                    sql = sql.replaceFirst("\\?", "NULL");
                } else if (value instanceof String || value instanceof Date) {
                    // 字符串和日期类型需要加引号
                    sql = sql.replaceFirst("\\?", "'" + value.toString() + "'");
                } else {
                    sql = sql.replaceFirst("\\?", value.toString());
                }
            }
        }
        return sql;
    }
}
```

在调试时的使用方法：

1.  在 Mapper 方法的断点处，按 Alt + F8 打开 Evaluate Expression
2.  输入以下代码获取完整 SQL：

```java
// 在调试时获取必要对象
Object parameter = XXX;  // 你的参数对象
MappedStatement ms = XXX;  // 从当前上下文获取
BoundSql boundSql = ms.getBoundSql(parameter);
Configuration configuration = ms.getConfiguration();

// 调用工具类获取完整SQL
String fullSql = SqlDebugUtil.getFullSql(boundSql, configuration);
System.out.println(fullSql);
```

如果你想要一个更简单的版本，可以在拦截器中直接调用：

```java
public class SimpleSqlDebugUtil {
    
    /**
     * 在MyBatis拦截器中使用的简化版本
     */
    public static String getFullSql(Invocation invocation) {
        try {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Object parameter = invocation.getArgs()[1];
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();
            
            return getFullSql(boundSql, configuration);
        } catch (Exception e) {
            return "Error getting SQL: " + e.getMessage();
        }
    }
    
    private static String getFullSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql();
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        
        if (parameterObject != null && parameterMappings != null) {
            sql = sql.replaceAll("\\n", " ").replaceAll("\\s+", " ");
            
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                Object value;
                
                try {
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    value = metaObject.getValue(propertyName);
                    
                    if (value == null) {
                        sql = sql.replaceFirst("\\?", "NULL");
                    } else if (value instanceof String || value instanceof Date) {
                        sql = sql.replaceFirst("\\?", "'" + value + "'");
                    } else {
                        sql = sql.replaceFirst("\\?", value.toString());
                    }
                } catch (Exception e) {
                    sql = sql.replaceFirst("\\?", "'" + parameterObject + "'");
                }
            }
        }
        return sql;
    }
}
```

在调试时使用：

```java
// 在拦截器断点处：
String sql = SimpleSqlDebugUtil.getFullSql(invocation);
System.out.println(sql);
```

这个工具类的特点：

1.  支持基本类型、字符串、日期等常见参数类型
2.  自动处理 NULL 值
3.  字符串和日期类型自动添加引号
4.  清理多余的换行和空格
5.  处理异常情况，避免调试时报错

使用建议：

1.  建议只在开发环境使用，不要在生产环境打印完整 SQL
2.  对于敏感数据，注意脱敏处理
3.  可以根据需要扩展支持更多数据类型
4.  如果 SQL 较复杂，建议在获取后做适当的格式化

## Prompt:
我需要是在执行mybatisdao之前调用，调用后执行dao，控制台就打日志

## Response:
我来写一个工具类，可以在调用 DAO 之前使用，并在后续执行时自动打印 SQL 日志：

```java
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class MybatisSqlHelper {
    
    public static void printSql() {
        // 在调试时，可以在 Evaluate Expression 中执行这个方法
        // 需要先获取当前 SqlSession
        SqlSession sqlSession = SpringUtil.getBean(SqlSession.class);
        Configuration configuration = sqlSession.getConfiguration();
        
        // 开启 SQL 日志
        System.setProperty("mybatis.sql.debug", "true");
        
        // 添加拦截器
        configuration.addInterceptor(new org.apache.ibatis.plugin.Interceptor() {
            @Override
            public Object intercept(org.apache.ibatis.plugin.Invocation invocation) throws Throwable {
                MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
                Object parameter = invocation.getArgs()[1];
                BoundSql boundSql = ms.getBoundSql(parameter);
                String sql = getFullSql(boundSql, configuration);
                System.out.println("\n>>> Full SQL: " + sql + "\n");
                return invocation.proceed();
            }

            @Override
            public Object plugin(Object target) {
                return org.apache.ibatis.plugin.Plugin.wrap(target, this);
            }

            @Override
            public void setProperties(java.util.Properties properties) {
            }
        });
    }
    
    private static String getFullSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql().replaceAll("\\n", " ").replaceAll("\\s+", " ");
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        
        if (parameterMappings != null && !parameterMappings.isEmpty()) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                Object value;
                
                if (boundSql.hasAdditionalParameter(propertyName)) {
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (parameterObject == null) {
                    value = null;
                } else if (configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                    value = parameterObject;
                } else {
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    value = metaObject.getValue(propertyName);
                }
                
                if (value == null) {
                    sql = sql.replaceFirst("\\?", "NULL");
                } else if (value instanceof String || value instanceof Date) {
                    sql = sql.replaceFirst("\\?", "'" + String.valueOf(value) + "'");
                } else {
                    sql = sql.replaceFirst("\\?", String.valueOf(value));
                }
            }
        }
        return sql;
    }
}
```

使用方法：

1.  在调试时，找到要执行的 DAO 方法的位置设置断点
2.  按 Alt + F8 打开 Evaluate Expression 窗口，执行：

```java
MybatisSqlHelper.printSql()
```

3.  继续执行代码，控制台就会打印完整的 SQL 语句

为了更方便使用，你还需要一个简单的 Spring 工具类：

```java
@Component
public class SpringUtil implements ApplicationContextAware {
    
    private static ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }
    
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
```

这样设计的好处是：

1.  不需要修改原有代码
2.  只在需要时开启 SQL 打印
3.  可以在调试时随时调用
4.  SQL 格式化后易于复制到数据库中执行

注意事项：

1.  这个方法会影响所有后续的 SQL 执行，直到应用重启
2.  如果需要关闭日志打印，可以重启应用或添加一个关闭方法
3.  生产环境不建议使用此方法
4.  对于敏感数据要注意安全性

如果你想要一个更简单的一次性使用版本：

```java
public class QuickSqlDebug {
    public static void debug(SqlSession sqlSession) {
        Configuration configuration = sqlSession.getConfiguration();
        configuration.addInterceptor(new org.apache.ibatis.plugin.Interceptor() {
            @Override
            public Object intercept(org.apache.ibatis.plugin.Invocation invocation) throws Throwable {
                MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
                Object parameter = invocation.getArgs()[1];
                BoundSql boundSql = ms.getBoundSql(parameter);
                System.out.println("SQL: " + boundSql.getSql());
                System.out.println("Parameters: " + parameter);
                return invocation.proceed();
            }

            @Override
            public Object plugin(Object target) {
                return org.apache.ibatis.plugin.Plugin.wrap(target, this);
            }

            @Override
            public void setProperties(java.util.Properties properties) {
            }
        });
    }
}
```

在调试时使用：

```java
// 在 Evaluate Expression 中：
QuickSqlDebug.debug(sqlSession)
```

这样就能实现你的需求，在执行 DAO 之前调用，后续的 SQL 执行都会打印日志了。

## Prompt:
提示说没有@intercepts被发现在工具类中

## Response:
我来修正这个问题，添加必要的 `@Intercepts` 注解：

```java
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.Date;
import java.util.List;
import java.util.Properties;

public class MybatisSqlHelper {
    
    public static void printSql() {
        SqlSession sqlSession = SpringUtil.getBean(SqlSession.class);
        Configuration configuration = sqlSession.getConfiguration();
        configuration.addInterceptor(new SqlDebugInterceptor());
    }

    @Intercepts({
            @Signature(type = Executor.class, method = "query",
                    args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
            @Signature(type = Executor.class, method = "update",
                    args = {MappedStatement.class, Object.class})
    })
    static class SqlDebugInterceptor implements Interceptor {
        @Override
        public Object intercept(Invocation invocation) throws Throwable {
            MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
            Object parameter = invocation.getArgs()[1];
            BoundSql boundSql = ms.getBoundSql(parameter);
            Configuration configuration = ms.getConfiguration();
            String sql = getFullSql(boundSql, configuration);
            System.out.println("\n>>> Full SQL: " + sql + "\n");
            return invocation.proceed();
        }

        @Override
        public Object plugin(Object target) {
            return Plugin.wrap(target, this);
        }

        @Override
        public void setProperties(Properties properties) {
        }
    }
    
    private static String getFullSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql().replaceAll("\\n", " ").replaceAll("\\s+", " ");
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        
        if (parameterMappings != null && !parameterMappings.isEmpty()) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                Object value;
                
                if (boundSql.hasAdditionalParameter(propertyName)) {
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (parameterObject == null) {
                    value = null;
                } else if (configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                    value = parameterObject;
                } else {
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    value = metaObject.getValue(propertyName);
                }
                
                if (value == null) {
                    sql = sql.replaceFirst("\\?", "NULL");
                } else if (value instanceof String || value instanceof Date) {
                    sql = sql.replaceFirst("\\?", "'" + String.valueOf(value) + "'");
                } else {
                    sql = sql.replaceFirst("\\?", String.valueOf(value));
                }
            }
        }
        return sql;
    }
}
```

使用方法：

1.  在调试断点处按 Alt + F8 打开 Evaluate Expression
2.  输入：

```java
MybatisSqlHelper.printSql()
```

3.  继续执行代码，此后的所有 Mybatis SQL 执行都会打印完整的可执行 SQL

主要改动：

1.  添加了 `@Intercepts` 注解
2.  将拦截器类提取为静态内部类
3.  完整定义了拦截的方法签名（query 和 update）

这样就能正确拦截到 MyBatis 的执行，并打印完整的 SQL 了。

## Prompt:
怎么只能打select不能打insertupdat语句，在执行前打，

## Response:
我来修正这个问题。让我们改进 MybatisSqlHelper，确保能打印所有类型的 SQL：

```java
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class MybatisSqlHelper {

    public static void enableSqlPrint(Object parameter, MappedStatement mappedStatement) {
        try {
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();
            String sql = getFullSql(boundSql, configuration);
            
            // 打印 SQL 类型和完整 SQL
            System.out.println("\n>>> SQL Type: " + mappedStatement.getSqlCommandType());
            System.out.println(">>> Full SQL: " + sql);
            System.out.println(">>> Parameters: " + parameter + "\n");
        } catch (Exception e) {
            System.out.println("Error printing SQL: " + e.getMessage());
        }
    }

    private static String getFullSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql().replaceAll("\\n", " ").replaceAll("\\s+", " ");
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        
        if (parameterMappings != null && !parameterMappings.isEmpty()) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                Object value;
                
                try {
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    
                    if (value == null) {
                        sql = sql.replaceFirst("\\?", "NULL");
                    } else if (value instanceof String || value instanceof Date 
                            || value instanceof java.sql.Date || value instanceof java.sql.Timestamp) {
                        // 处理所有日期类型
                        sql = sql.replaceFirst("\\?", "'" + String.valueOf(value) + "'");
                    } else if (value instanceof Boolean) {
                        // 特殊处理布尔类型
                        sql = sql.replaceFirst("\\?", value.toString());
                    } else {
                        sql = sql.replaceFirst("\\?", String.valueOf(value));
                    }
                } catch (Exception e) {
                    // 如果解析参数出错，保留问号
                    sql = sql.replaceFirst("\\?", "?[Error:(" + propertyName + ")]");
                }
            }
        }
        return sql;
    }
}
```

在执行 Mapper 方法之前使用（在断点处的 Evaluate Expression 中）：

```java
// 对于普通的 Mapper 方法：
Object parameter = 你的参数对象;  // 可以是实体类、Map等
MappedStatement mappedStatement = sqlSession.getConfiguration()
    .getMappedStatement("你的Mapper名.方法名"); // 例如: "com.example.UserMapper.insert"
MybatisSqlHelper.enableSqlPrint(parameter, mappedStatement);

// 或者更简单的方式，如果你在断点处已经有这些对象：
MybatisSqlHelper.enableSqlPrint(parameterObject, ms);
```

具体使用示例：

1.  在执行 insert 之前：

```java
// 在你的断点处按 Alt+F8，输入：
UserEntity user = new UserEntity();
user.setName("test");
user.setAge(25);
MappedStatement ms = sqlSession.getConfiguration()
    .getMappedStatement("com.example.UserMapper.insert");
MybatisSqlHelper.enableSqlPrint(user, ms);
```

2.  在执行 update 之前：

```java
// 在你的断点处按 Alt+F8，输入：
Map<String, Object> params = new HashMap<>();
params.put("id", 1);
params.put("name", "newName");
MappedStatement ms = sqlSession.getConfiguration()
    .getMappedStatement("com.example.UserMapper.update");
MybatisSqlHelper.enableSqlPrint(params, ms);
```

这个版本的改进：

1.  移除了拦截器，直接在执行前打印
2.  支持所有 SQL 类型（SELECT/INSERT/UPDATE/DELETE）
3.  更好的参数处理，包括各种日期类型
4.  添加了错误处理，不会因为解析错误而中断
5.  显示 SQL 类型和参数信息
6.  更清晰的输出格式

注意事项：

1.  需要在执行 SQL 之前调用
2.  mappedStatement 的名字要和你的 Mapper 接口完全匹配
3.  parameter 对象要和实际执行时使用的参数一致
4.  如果参数是复杂对象，确保所有必要的字段都已设置

## Prompt:
sqlsession是什么，怎么引入

## Response:
对，我来说明下如何在 SpringBoot 中获取 SqlSession：

1.  首先需要注入 SqlSessionFactory：

```java
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.ibatis.mapping.MappedStatement;

public class MybatisSqlHelper {
    
    // 用于存储 SqlSessionFactory 的静态变量
    private static SqlSessionFactory sqlSessionFactory;
    
    // 通过 Spring 注入 SqlSessionFactory
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        MybatisSqlHelper.sqlSessionFactory = sqlSessionFactory;
    }

    public static void printSql(String mapperClassName, String methodName, Object parameter) {
        try {
            // 获取 SqlSession
            String statementId = mapperClassName + "." + methodName;
            MappedStatement mappedStatement = sqlSessionFactory.getConfiguration()
                .getMappedStatement(statementId);
                
            // 打印 SQL
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            String sql = getFullSql(boundSql, mappedStatement.getConfiguration());
            System.out.println("\n>>> SQL Type: " + mappedStatement.getSqlCommandType());
            System.out.println(">>> Full SQL: " + sql + "\n");
        } catch (Exception e) {
            System.out.println("Error printing SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ... 其他方法保持不变
}
```

2.  使用方法（在调试时）：

```java
// 在 Evaluate Expression 中输入：
MybatisSqlHelper.printSql(
    "com.example.mapper.UserMapper",  // Mapper类的全限定名
    "insert",                         // 方法名
    userObject                        // 参数对象
);
```

3.  如果你想要更简单的使用方式，可以在 Service 层添加：

```java
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public void insert(User user) {
        // 在执行前打印 SQL
        MybatisSqlHelper.printSql(
            UserMapper.class.getName(),
            "insert",
            user
        );
        // 执行实际的插入
        userMapper.insert(user);
    }
}
```

完整的工具类：

```java
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class MybatisSqlHelper {
    
    private static SqlSessionFactory sqlSessionFactory;
    
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        MybatisSqlHelper.sqlSessionFactory = sqlSessionFactory;
    }

    public static void printSql(String mapperClassName, String methodName, Object parameter) {
        try {
            String statementId = mapperClassName + "." + methodName;
            MappedStatement mappedStatement = sqlSessionFactory.getConfiguration()
                .getMappedStatement(statementId);
            
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            String sql = getFullSql(boundSql, mappedStatement.getConfiguration());
            
            System.out.println("\n>>> Mapper: " + mapperClassName);
            System.out.println(">>> Method: " + methodName);
            System.out.println(">>> SQL Type: " + mappedStatement.getSqlCommandType());
            System.out.println(">>> Full SQL: " + sql);
            System.out.println(">>> Parameters: " + parameter + "\n");
        } catch (Exception e) {
            System.out.println("Error printing SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getFullSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql().replaceAll("\\n", " ").replaceAll("\\s+", " ");
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        
        if (parameterMappings != null && !parameterMappings.isEmpty()) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                Object value;
                
                try {
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    
                    if (value == null) {
                        sql = sql.replaceFirst("\\?", "NULL");
                    } else if (value instanceof String || value instanceof Date 
                            || value instanceof java.sql.Date || value instanceof java.sql.Timestamp) {
                        sql = sql.replaceFirst("\\?", "'" + String.valueOf(value) + "'");
                    } else {
                        sql = sql.replaceFirst("\\?", String.valueOf(value));
                    }
                } catch (Exception e) {
                    sql = sql.replaceFirst("\\?", "?[Error:(" + propertyName + ")]");
                }
            }
        }
        return sql;
    }
}
```

使用步骤：

1.  把这个工具类放在你的项目中
2.  确保类上有 @Component 注解
3.  Spring 会自动注入 SqlSessionFactory
4.  在调试时使用：

```java
// 在断点处按 Alt+F8，输入：
MybatisSqlHelper.printSql(
    "com.example.mapper.UserMapper",
    "insert",
    user
);
```

这样设计的好处是：

1.  不需要手动获取 SqlSession
2.  使用更简单，只需要提供 Mapper 类名、方法名和参数
3.  自动处理所有类型的 SQL
4.  带有详细的错误处理

## Prompt:
// 在你的断点处按 Alt+F8，输入： Map<String, Object> params = new HashMap<>(); params.put("id", 1); params.put("name", "newName"); MappedStatement ms = sqlSession.getConfiguration() .getMappedStatement("com.example.UserMapper.update"); MybatisSqlHelper.enableSqlPrint(params, ms);我要知道这个的sqlsession

## Response:
在 SpringBoot 项目中获取 SqlSession 的方法有几种，我来展示在调试时最方便的方式：

1.  首先在你的项目中添加一个工具类：

```java
@Component
public class SpringUtil implements ApplicationContextAware {
    
    private static ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }
    
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
```

2.  然后在调试时（Evaluate Expression 窗口中）可以这样获取 SqlSession：

```java
// 获取 SqlSession
SqlSessionTemplate sqlSession = SpringUtil.getBean(SqlSessionTemplate.class);

// 然后就可以执行你的代码了
Map<String, Object> params = new HashMap<>();
params.put("id", 1);
params.put("name", "newName");
MappedStatement ms = sqlSession.getConfiguration()
    .getMappedStatement("com.example.UserMapper.update");
MybatisSqlHelper.enableSqlPrint(params, ms);
```

或者另一种方式，直接从 SqlSessionFactory 获取：

```java
// 获取 SqlSessionFactory
SqlSessionFactory sqlSessionFactory = SpringUtil.getBean(SqlSessionFactory.class);
SqlSession sqlSession = sqlSessionFactory.openSession();

// 后续代码相同
Map<String, Object> params = new HashMap<>();
params.put("id", 1);
params.put("name", "newName");
MappedStatement ms = sqlSession.getConfiguration()
    .getMappedStatement("com.example.UserMapper.update");
MybatisSqlHelper.enableSqlPrint(params, ms);
```

注意事项：

1.  确保 SpringUtil 类有 @Component 注解
2.  确保它被 Spring 扫描到（在启动类所在包或子包中）
3.  如果使用 sqlSessionFactory.openSession()，记得使用完后关闭 session
4.  推荐使用第一种方式 (SqlSessionTemplate)，因为它是 Spring 管理的线程安全 bean

## Prompt:
mapstatement拿不到

## Response:
我来帮你优化一个更简单的方式，不需要手动获取 MappedStatement：

```java
@Component
public class MybatisSqlHelper {
    
    private static SqlSessionFactory sqlSessionFactory;
    
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        MybatisSqlHelper.sqlSessionFactory = sqlSessionFactory;
    }

    /**
     * 打印完整SQL，使用完整的Mapper路径
     */
    public static void printSql(Object parameter, String fullMapperPath) {
        try {
            MappedStatement mappedStatement = sqlSessionFactory.getConfiguration()
                    .getMappedStatement(fullMapperPath);
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();
            
            String sql = getFullSql(boundSql, configuration);
            System.out.println("\n>>> SQL Type: " + mappedStatement.getSqlCommandType());
            System.out.println(">>> Mapper Method: " + fullMapperPath);
            System.out.println(">>> Full SQL: " + sql + "\n");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String getFullSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql().replaceAll("\\n", " ").replaceAll("\\s+", " ");
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        
        if (parameterMappings != null && !parameterMappings.isEmpty()) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                Object value;
                
                try {
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    
                    if (value == null) {
                        sql = sql.replaceFirst("\\?", "NULL");
                    } else if (value instanceof String || value instanceof Date) {
                        sql = sql.replaceFirst("\\?", "'" + String.valueOf(value) + "'");
                    } else {
                        sql = sql.replaceFirst("\\?", String.valueOf(value));
                    }
                } catch (Exception e) {
                    sql = sql.replaceFirst("\\?", "?[Error:" + propertyName + "]");
                }
            }
        }
        return sql;
    }
}
```

在调试时使用（在断点处按 Alt+F8）：

```java
// 对于更新操作
Map<String, Object> params = new HashMap<>();
params.put("id", 1);
params.put("name", "newName");
MybatisSqlHelper.printSql(params, "com.example.mapper.UserMapper.updateUser");

// 对于插入操作
User user = new User();
user.setName("test");
user.setAge(25);
MybatisSqlHelper.printSql(user, "com.example.mapper.UserMapper.insert");

// 对于查询操作
Map<String, Object> queryParams = new HashMap<>();
queryParams.put("id", 1);
MybatisSqlHelper.printSql(queryParams, "com.example.mapper.UserMapper.selectById");
```

这个版本的优点：

1.  不需要手动获取 SqlSession 和 MappedStatement
2.  只需要提供参数和完整的 Mapper 方法路径
3.  使用更简单直观
4.  更好的错误处理和提示

需要注意：

1.  Mapper 路径要写完整，包括包名
2.  方法名要和 Mapper 接口中的方法名完全一致
3.  参数对象要和实际调用时的参数类型一致

如果不确定 Mapper 的完整路径，可以在调试时这样查看：

```java
// 在你的 Mapper 接口处或使用处设置断点，然后在 Evaluate Expression 中输入：
YourMapper.class.getName() + ".方法名"
```

这样就能得到完整的路径了。

## Prompt:
我有2个数据源时，有2 个sqlsqlsqlsessionsingbea提示冲突，因为工厂，注入

## Response:
对于多数据源的情况，我们需要修改一下工具类来指定使用哪个数据源。以下是支持多数据源的版本：

```java
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class MybatisSqlHelper {
    
    // 存储多个数据源的 SqlSessionFactory
    private static SqlSessionFactory primarySqlSessionFactory;
    private static SqlSessionFactory secondarySqlSessionFactory;
    
    @Autowired
    public void setPrimarySqlSessionFactory(@Qualifier("primarySqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        MybatisSqlHelper.primarySqlSessionFactory = sqlSessionFactory;
    }
    
    @Autowired
    public void setSecondarySqlSessionFactory(@Qualifier("secondarySqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        MybatisSqlHelper.secondarySqlSessionFactory = sqlSessionFactory;
    }

    /**
     * 打印主数据源的SQL
     */
    public static void printPrimarySql(Object parameter, String fullMapperPath) {
        printSql(parameter, fullMapperPath, primarySqlSessionFactory);
    }

    /**
     * 打印从数据源的SQL
     */
    public static void printSecondarySql(Object parameter, String fullMapperPath) {
        printSql(parameter, fullMapperPath, secondarySqlSessionFactory);
    }

    private static void printSql(Object parameter, String fullMapperPath, SqlSessionFactory sqlSessionFactory) {
        try {
            MappedStatement mappedStatement = sqlSessionFactory.getConfiguration()
                    .getMappedStatement(fullMapperPath);
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();
            
            String sql = getFullSql(boundSql, configuration);
            System.out.println("\n>>> DataSource: " + (sqlSessionFactory == primarySqlSessionFactory ? "Primary" : "Secondary"));
            System.out.println(">>> SQL Type: " + mappedStatement.getSqlCommandType());
            System.out.println(">>> Mapper Method: " + fullMapperPath);
            System.out.println(">>> Full SQL: " + sql + "\n");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getFullSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql().replaceAll("\\n", " ").replaceAll("\\s+", " ");
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        
        if (parameterMappings != null && !parameterMappings.isEmpty()) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                Object value;
                
                try {
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    
                    if (value == null) {
                        sql = sql.replaceFirst("\\?", "NULL");
                    } else if (value instanceof String || value instanceof Date) {
                        sql = sql.replaceFirst("\\?", "'" + String.valueOf(value) + "'");
                    } else {
                        sql = sql.replaceFirst("\\?", String.valueOf(value));
                    }
                } catch (Exception e) {
                    sql = sql.replaceFirst("\\?", "?[Error:" + propertyName + "]");
                }
            }
        }
        return sql;
    }
}
```

使用方法（在调试时）：

```java
// 对于主数据源的SQL
Map<String, Object> params = new HashMap<>();
params.put("id", 1);
params.put("name", "test");
// 使用主数据源打印SQL
MybatisSqlHelper.printPrimarySql(params, "com.example.mapper.primary.UserMapper.update");

// 对于从数据源的SQL
User user = new User();
user.setName("test");
// 使用从数据源打印SQL
MybatisSqlHelper.printSecondarySql(user, "com.example.mapper.secondary.OrderMapper.insert");
```

确保你的多数据源配置类中有正确的 @Qualifier 注解：

```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    @Primary
    @Qualifier("primarySqlSessionFactory")
    public SqlSessionFactory primarySqlSessionFactory() {
        // 主数据源配置
    }
    
    @Bean
    @Qualifier("secondarySqlSessionFactory")
    public SqlSessionFactory secondarySqlSessionFactory() {
        // 从数据源配置
    }
}
```

这样修改后：

1.  可以分别处理两个数据源的SQL
2.  通过不同的方法名明确指定要使用哪个数据源
3.  输出中会显示使用的是哪个数据源
4.  避免了 SqlSessionFactory 的注入冲突

使用时只需要根据你要调试的 Mapper 属于哪个数据源，选择对应的方法即可。

