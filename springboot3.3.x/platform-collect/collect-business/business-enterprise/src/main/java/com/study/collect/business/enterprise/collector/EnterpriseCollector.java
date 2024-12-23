package com.study.collect.business.enterprise.collector;

import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.core.annotation.Collector;
import com.study.collect.core.cache.annotation.Cache;
import com.study.collect.core.cache.annotation.CacheLock;
import com.study.collect.core.collector.AbstractCollector;

//@Collector(type = "enterprise")
//@Component
//@RequiredArgsConstructor
//public class EnterpriseCollector implements ICollector<String, Enterprise> {
//
//    private final EnterpriseRepository repository;
//
//    @Override
//    public Enterprise collect(String code) {
//        return repository.findByCode(code);
//    }
//
//    @Override
//    public String getType() {
//        return "enterprise";
//    }
//}



// 2. Collector - 增加缓存和分布式锁
@Collector(type = "enterprise")
public class EnterpriseCollector extends AbstractCollector<String, Enterprise> {

//    @Cache(key = "enterprise:#{#code}")  // 缓存注解
//    @CacheLock(key = "lock:enterprise:#{#code}")  // 分布式锁注解
//    public Enterprise collect(String code) {
//        // 采集逻辑
//        return doCollect(code);
//    }

    @Cache(key = "enterprise:#{#code}")
    @CacheLock(key = "lock:enterprise:#{#code}")
    @Override
//    protected Enterprise doCollect(String code) {
    public Enterprise collect(String code) {
        // 1. 调用外部接口采集数据
        Enterprise data = collectFromApi(code);
        // 2. 设置版本号
        data.setVersion(generateVersion());
        return data;
    }

    private String generateVersion() {
        return "1.0";
    }

    private Enterprise collectFromApi(String code) {
        return new Enterprise();
    }
}
