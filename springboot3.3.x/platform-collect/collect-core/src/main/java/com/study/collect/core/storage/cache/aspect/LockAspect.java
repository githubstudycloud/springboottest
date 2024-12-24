package com.study.collect.core.storage.cache.aspect;

import com.study.collect.core.storage.cache.annotation.CacheLock;
import com.study.collect.core.storage.cache.lock.DistributedLock;
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

@Slf4j
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
            boolean locked = lock.tryLock(key,
                    cacheLock.waitTime(),
                    cacheLock.leaseTime(),
                    cacheLock.timeUnit());

            if (!locked) {
                throw new RuntimeException("Failed to acquire lock: " + key);
            }

            return point.proceed();
        } finally {
            lock.unlock(key);
        }
    }


    // key解析实现，与CacheAspect中相同
    private String parseKey(String prefix, String key, ProceedingJoinPoint point) {
        if (key.isEmpty()) {
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
        return prefix.isEmpty() ? parsedKey : prefix + ":" + parsedKey;
    }
}