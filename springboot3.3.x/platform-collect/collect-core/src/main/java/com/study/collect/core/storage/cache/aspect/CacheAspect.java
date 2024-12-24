package com.study.collect.core.storage.cache.aspect;

import com.study.collect.core.storage.cache.annotation.Cache;
import com.study.collect.core.storage.cache.annotation.CacheEvict;
import com.study.collect.core.storage.cache.manager.CacheManager;
import com.study.collect.core.storage.cache.model.CacheOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CacheAspect {

    private final CacheManager cacheManager;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 缓存切面逻辑
     */
    @Around("@annotation(cache)")
    public Object doCache(ProceedingJoinPoint point, Cache cache) throws Throwable {
        // 解析key
        String key = parseKey(cache.prefix(), cache.key(), point);

        // 获取返回类型
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<?> returnType = signature.getReturnType();

        // 构造缓存参数
        CacheOptions options = CacheOptions.builder()
                .expiration(cache.expire())
                .timeUnit(cache.timeUnit())
                .build();

        // 调用底层缓存逻辑
        try {
            return getFromCache(point, key, returnType, options);
        } catch (Exception e) {
            log.error("Cache operation failed for key: {}", key, e);
            // 缓存异常时，直接执行原方法
            return point.proceed();
        }
    }

    /**
     * 缓存清理切面逻辑
     */
    @Around("@annotation(cacheEvict)")
    public Object doEvict(ProceedingJoinPoint point, CacheEvict cacheEvict) throws Throwable {
        // 如果 beforeInvocation = true，则先清理再执行，否则先执行再清理
        boolean beforeInvocation = cacheEvict.beforeInvocation();
        if (beforeInvocation) {
            evictCache(cacheEvict, point);
        }
        Object result = point.proceed();
        if (!beforeInvocation) {
            evictCache(cacheEvict, point);
        }
        return result;
    }

    /**
     * 执行具体的缓存清理逻辑
     */
    private void evictCache(CacheEvict cacheEvict, ProceedingJoinPoint point) {
        try {
            if (cacheEvict.allEntries()) {
                cacheManager.removeByPrefix(cacheEvict.prefix());
            } else {
                String key = parseKey(cacheEvict.prefix(), cacheEvict.key(), point);
                cacheManager.remove(key);
            }
        } catch (Exception e) {
            log.error("Failed to evict cache", e);
        }
    }

    /**
     * 解析 SpEL 表达式得到缓存 key
     */
    private String parseKey(String prefix, String key, ProceedingJoinPoint point) {
        if (!StringUtils.hasText(key)) {
            return prefix;
        }
        try {
            EvaluationContext context = new StandardEvaluationContext();
            MethodSignature signature = (MethodSignature) point.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] args = point.getArgs();
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            // 根据 SpEL 表达式计算出的 key
            String parsedKey = parser.parseExpression(key).getValue(context, String.class);
            // 如果 prefix 不为空，则使用 prefix:parsedKey
            return StringUtils.hasText(prefix) ? prefix + ":" + parsedKey : parsedKey;
        } catch (Exception e) {
            log.error("Failed to parse cache key: {}", key, e);
            throw new IllegalArgumentException("Invalid cache key expression", e);
        }
    }

    /**
     * 使用泛型方法，避免不安全的类型转换警告
     */
    @SuppressWarnings("unchecked")
    private <T> T getFromCache(ProceedingJoinPoint point, String key, Class<?> returnType, CacheOptions options) {
        return cacheManager.get(key, (Class<T>) returnType, () -> {
            try {
                return (T) point.proceed();
            } catch (Throwable e) {
                log.error("Method execution failed at: {}", point.getSignature(), e);
                throw new IllegalStateException("Cache method execution failed", e);
            }
        }, options);
    }
}
