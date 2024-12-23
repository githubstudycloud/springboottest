package com.study.collect.core.cache.handler;

import com.study.collect.core.cache.annotation.CacheLock;
import com.study.collect.core.cache.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

// 2. 分布式锁切面
@Aspect
@Component
@RequiredArgsConstructor
public class LockAspect {

    private final DistributedLock lock;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(cacheLock)")
    public Object doLock(ProceedingJoinPoint point, CacheLock cacheLock) throws Throwable {
        String key = parseKey(cacheLock.prefix(), cacheLock.key(), point);

        try {
            boolean isLocked = lock.tryLock(key,
                    cacheLock.waitTime(),
                    cacheLock.leaseTime(),
                    cacheLock.timeUnit());

            if (!isLocked) {
//                throw new LockException("Get lock failed: " + key);
                throw new RuntimeException("Get lock failed: " + key);
            }

            return point.proceed();
        } finally {
            lock.unlock(key);
        }
    }

    private String parseKey(String prefix, String key, ProceedingJoinPoint point) {
        // 同CacheAspect中的解析逻辑
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