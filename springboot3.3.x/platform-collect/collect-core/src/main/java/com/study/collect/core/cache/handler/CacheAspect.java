package com.study.collect.core.cache.handler;

import com.study.collect.core.cache.annotation.Cache;
import com.study.collect.core.cache.annotation.CacheEvict;
import com.study.collect.core.cache.manager.CacheManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

// 1. 缓存切面
@Aspect
@Component
@RequiredArgsConstructor
public class CacheAspect {

    private final CacheManager cacheManager;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(cache)")
    public Object doCache(ProceedingJoinPoint point, Cache cache) throws Throwable {
        // 1. 解析缓存key
        String key = parseKey(cache.prefix(), cache.key(), point);

        // 2. 尝试获取缓存
        Class<?> returnType = ((MethodSignature)point.getSignature()).getReturnType();
        Object value = cacheManager.get(key, returnType);
        if (value != null) {
            return value;
        }

        // 3. 执行方法
        value = point.proceed();

        // 4. 设置缓存
        if (value != null) {
            cacheManager.set(key, value, cache.expire(), cache.timeUnit());
        }

        return value;
    }

    @Around("@annotation(cacheEvict)")
    public Object doEvict(ProceedingJoinPoint point, CacheEvict cacheEvict) throws Throwable {
        // 是否在方法执行前清除缓存
        if (cacheEvict.beforeInvocation()) {
            evictCache(cacheEvict, point);
            return point.proceed();
        }

        try {
            Object result = point.proceed();
            evictCache(cacheEvict, point);
            return result;
        } catch (Throwable e) {
            if (cacheEvict.beforeInvocation()) {
                evictCache(cacheEvict, point);
            }
            throw e;
        }
    }

    private void evictCache(CacheEvict cacheEvict, ProceedingJoinPoint point) {
        if (cacheEvict.allEntries()) {
            // 清除前缀下所有缓存
            cacheManager.deleteByPrefix(cacheEvict.prefix());
        } else {
            // 清除指定key的缓存
            String key = parseKey(cacheEvict.prefix(), cacheEvict.key(), point);
            cacheManager.delete(key);
        }
    }

    private String parseKey(String prefix, String key, ProceedingJoinPoint point) {
        // SpEL解析key表达式
        if (StringUtils.isEmpty(key)) {
            return prefix;
        }

        EvaluationContext context = new StandardEvaluationContext();
        MethodSignature signature = (MethodSignature) point.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = point.getArgs();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        String parsedKey = parser.parseExpression(key).getValue(context, String.class);
        return StringUtils.isEmpty(prefix) ? parsedKey : prefix + ":" + parsedKey;
    }
}