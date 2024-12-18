package com.study.collect.infrastructure.schedule.elastic;

import com.study.collect.core.strategy.balance.LoadBalanceStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器
 */
@Slf4j
@Component
public class ConsistentHashLoadBalancer implements LoadBalanceStrategy {

    private final TreeMap<Long, String> hashRing = new TreeMap<>();
    private final int numberOfReplicas = 160; // 虚拟节点数

    /**
     * 添加节点
     */
    public synchronized void addNode(String node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            long hash = hash(node + i);
            hashRing.put(hash, node);
        }
        log.info("Node added to hash ring: {}", node);
    }

    /**
     * 移除节点
     */
    public synchronized void removeNode(String node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            long hash = hash(node + i);
            hashRing.remove(hash);
        }
        log.info("Node removed from hash ring: {}", node);
    }

    /**
     * 获取负载节点
     */
    @Override
    public String selectNode(String key) {
        if (hashRing.isEmpty()) {
            return null;
        }

        long hash = hash(key);
        Map.Entry<Long, String> entry = hashRing.ceilingEntry(hash);
        if (entry == null) {
            entry = hashRing.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * MurmurHash算法
     */
    private long hash(String key) {
        ByteBuffer buf = ByteBuffer.wrap(key.getBytes());
        int seed = 0x1234ABCD;

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        return h;
    }
}
