package com.study.collect.business.testcase.utils;

import com.study.collect.business.testcase.constant.CollectionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RateLimiter {
    private final int permitsPerMinute;
    private final ConcurrentLinkedQueue<Long> timestamps;
    private final AtomicInteger currentPermits;
    private final ScheduledExecutorService scheduler;

    public RateLimiter() {
        this.permitsPerMinute = CollectionConstants.HTTP_MAX_REQUESTS_PER_MINUTE;
        this.timestamps = new ConcurrentLinkedQueue<>();
        this.currentPermits = new AtomicInteger(0);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("rate-limiter-cleaner");
            thread.setDaemon(true);
            return thread;
        });

        // 定期清理过期的时间戳
        scheduler.scheduleAtFixedRate(
                this::cleanup,
                1,
                1,
                TimeUnit.MINUTES
        );
    }

    /**
     * 获取许可
     */
    public void acquire() throws InterruptedException {
        while (!tryAcquire()) {
            Thread.sleep(5000); // 等待100ms后重试
        }
    }

    /**
     * 尝试获取许可
     */
    public boolean tryAcquire() {
        cleanup(); // 清理过期的时间戳

        long now = System.currentTimeMillis();
        int currentCount = currentPermits.get();

        if (currentCount >= permitsPerMinute) {
            return false;
        }

        if (currentPermits.incrementAndGet() <= permitsPerMinute) {
            timestamps.offer(now);
            return true;
        } else {
            currentPermits.decrementAndGet();
            return false;
        }
    }

    /**
     * 清理过期的时间戳
     */
    private void cleanup() {
        long now = System.currentTimeMillis();
        long oneMinuteAgo = now - TimeUnit.MINUTES.toMillis(1);

        // 移除一分钟前的时间戳
        while (!timestamps.isEmpty() && timestamps.peek() < oneMinuteAgo) {
            timestamps.poll();
            currentPermits.decrementAndGet();
        }
    }

    /**
     * 获取当前速率
     */
    public int getCurrentRate() {
        cleanup();
        return currentPermits.get();
    }

    /**
     * 获取剩余许可数
     */
    public int getAvailablePermits() {
        cleanup();
        return permitsPerMinute - currentPermits.get();
    }

    /**
     * 等待直到有可用许可
     */
    public void waitForPermit() throws InterruptedException {
        while (getCurrentRate() >= permitsPerMinute) {
            Thread.sleep(100);
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}