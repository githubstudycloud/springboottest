/**
 * 集合版本注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CollectionVersion {
    /**
     * 参数名称，如果指定则按参数名查找
     */
    String paramName() default "";
    
    /**
     * 参数位置，如果未指定参数名则按位置查找
     */
    int paramIndex() default 0;
}

/**
 * 集合版本切面
 */
@Aspect
@Component
@Slf4j
public class CollectionVersionAspect {
    
    @Around("@annotation(CollectionVersion)")
    public Object aroundCollectionVersion(ProceedingJoinPoint joinPoint) throws Throwable {
        CollectionVersion annotation = ((MethodSignature) joinPoint.getSignature())
            .getMethod().getAnnotation(CollectionVersion.class);
            
        try {
            String version = resolveVersion(joinPoint, annotation);
            if (version == null) {
                throw new IllegalArgumentException("Failed to resolve collection version");
            }
            
            CollectionStrategy.setVersion(version);
            return joinPoint.proceed();
        } finally {
            CollectionStrategy.clearVersion();
        }
    }
    
    /**
     * 解析版本信息
     */
    private String resolveVersion(ProceedingJoinPoint joinPoint, CollectionVersion annotation) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        
        // 如果没有参数，抛出异常
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("No parameters found in method: " + signature.getMethod().getName());
        }

        // 1. 尝试按参数名查找
        if (StringUtils.hasText(annotation.paramName())) {
            String[] parameterNames = signature.getParameterNames();
            for (int i = 0; i < parameterNames.length; i++) {
                if (annotation.paramName().equals(parameterNames[i])) {
                    return resolveVersionFromObject(args[i]);
                }
            }
            log.warn("Parameter name '{}' not found in method: {}", 
                    annotation.paramName(), signature.getMethod().getName());
        }
        
        // 2. 按参数位置查找
        int paramIndex = annotation.paramIndex();
        if (paramIndex >= 0 && paramIndex < args.length) {
            return resolveVersionFromObject(args[paramIndex]);
        }
        
        // 3. 尝试从第一个参数查找版本信息
        return resolveVersionFromObject(args[0]);
    }
    
    /**
     * 从对象中解析版本信息
     */
    private String resolveVersionFromObject(Object arg) {
        if (arg == null) {
            return null;
        }
        
        // 直接是String类型
        if (arg instanceof String) {
            return (String) arg;
        }
        
        // 如果是请求对象，尝试获取version字段
        try {
            // 通过反射查找version字段
            Field versionField = ReflectionUtils.findField(arg.getClass(), "version");
            if (versionField != null) {
                ReflectionUtils.makeAccessible(versionField);
                Object value = versionField.get(arg);
                return value != null ? value.toString() : null;
            }
            
            // 尝试通过getter方法获取
            Method getVersion = ReflectionUtils.findMethod(arg.getClass(), "getVersion");
            if (getVersion != null) {
                ReflectionUtils.makeAccessible(getVersion);
                Object value = getVersion.invoke(arg);
                return value != null ? value.toString() : null;
            }
            
            // 尝试查找uriVersion字段
            Field uriVersionField = ReflectionUtils.findField(arg.getClass(), "uriVersion");
            if (uriVersionField != null) {
                ReflectionUtils.makeAccessible(uriVersionField);
                Object value = uriVersionField.get(arg);
                return value != null ? value.toString() : null;
            }
        } catch (Exception e) {
            log.debug("Failed to resolve version from object: {}", arg, e);
        }
        
        return null;
    }
}

// 使用示例
@Service
public class UriService {
    
    // 按参数名指定
    @CollectionVersion(paramName = "version")
    public void saveData(String version, UriEntity entity) {
        repository.save(entity);
    }
    
    // 按参数位置指定
    @CollectionVersion(paramIndex = 1)
    public void saveData(UriEntity entity, String version) {
        repository.save(entity);
    }
    
    // 默认使用第一个参数
    @CollectionVersion
    public void saveData(String version, UriEntity entity) {
        repository.save(entity);
    }
    
    // 从对象中获取版本信息
    @CollectionVersion
    public void saveData(UriEntity entity) {  // 会尝试从entity中获取version字段
        repository.save(entity);
    }
}