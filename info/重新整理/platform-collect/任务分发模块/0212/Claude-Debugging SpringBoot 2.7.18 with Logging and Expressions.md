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

