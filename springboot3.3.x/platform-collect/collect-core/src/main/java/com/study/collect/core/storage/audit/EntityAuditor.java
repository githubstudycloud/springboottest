package com.study.collect.core.storage.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EntityAuditor implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // 获取当前操作用户，可以从SecurityContext或ThreadLocal中获取
        return Optional.of("system");
    }
}
