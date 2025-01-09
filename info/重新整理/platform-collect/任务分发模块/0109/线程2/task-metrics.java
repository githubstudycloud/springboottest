import lombok.Data;
import lombok.Builder;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class TaskMetrics {
    // 任务统计
    private final AtomicInteger totalTasks = new AtomicInteger(0);
    private final AtomicInteger runningTasks = new AtomicInteger(0);
    private final AtomicInteger completedTasks = new AtomicInteger(0);
    private final AtomicInteger failedTasks = new AtomicInteger(0);
    private final AtomicInteger retriedTasks = new AtomicInteger(0);
    
    // 时间统计
    private final Instant startTime = Instant.now();
    private volatile Instant lastUpdateTime = Instant.now();
    private final AtomicLong totalProcessingTimeMs = new AtomicLong(0);
    private final AtomicLong maxTaskTimeMs = new AtomicLong(0);
    private final AtomicLong minTaskTimeMs = new AtomicLong(Long.MAX_VALUE);
    
    // 分层任务统计
    private final Map<Integer, LevelMetrics> levelMetrics = new ConcurrentHashMap<>();
    
    // 自定义指标
    private final Map<String, AtomicLong> customMetrics = new ConcurrentHashMap<>();
    
    public void recordTaskStart(int level) {
        totalTasks.incrementAndGet();
        runningTasks.incrementAndGet();
        getLevelMetrics(level).recordTaskStart();
    }
    
    public void recordTaskEnd(int level, Duration duration, boolean success) {
        runningTasks.decrementAndGet();
        if (success) {
            completedTasks.incrementAndGet();
        } else {
            failedTasks.incrementAndGet();
        }
        
        long durationMs = duration.toMillis();
        totalProcessingTimeMs.addAndGet(durationMs);
        updateMinMaxTaskTime(durationMs);
        getLevelMetrics(level).recordTaskEnd(duration, success);
        lastUpdateTime = Instant.now();
    }
    
    public void recordRetry() {
        retriedTasks.incrementAndGet();
    }
    
    public void incrementCustomMetric(String name) {
        customMetrics.computeIfAbsent(name, k -> new AtomicLong()).incrementAndGet();
    }
    
    public void incrementCustomMetric(String name, long delta) {
        customMetrics.computeIfAbsent(name, k -> new AtomicLong()).addAndGet(delta);
    }
    
    private LevelMetrics getLevelMetrics(int level) {
        return levelMetrics.computeIfAbsent(level, k -> new LevelMetrics());
    }
    
    private void updateMinMaxTaskTime(long durationMs) {
        long currentMax;
        do {
            currentMax = maxTaskTimeMs.get();
            if (durationMs <= currentMax) break;
        } while (!maxTaskTimeMs.compareAndSet(currentMax, durationMs));
        
        long currentMin;
        do {
            currentMin = minTaskTimeMs.get();
            if (durationMs >= currentMin) break;
        } while (!minTaskTimeMs.compareAndSet(currentMin, durationMs));
    }
    
    @Data
    private static class LevelMetrics {
        private final AtomicInteger totalTasks = new AtomicInteger(0);
        private final AtomicInteger runningTasks = new AtomicInteger(0);
        private final AtomicInteger completedTasks = new AtomicInteger(0);
        private final AtomicInteger failedTasks = new AtomicInteger(0);
        private final AtomicLong totalProcessingTimeMs = new AtomicLong(0);
        
        public void recordTaskStart() {
            totalTasks.incrementAndGet();
            runningTasks.incrementAndGet();
        }
        
        public void recordTaskEnd(Duration duration, boolean success) {
            runningTasks.decrementAndGet();
            if (success) {
                completedTasks.incrementAndGet();
            } else {
                failedTasks.incrementAndGet();
            }
            totalProcessingTimeMs.addAndGet(duration.toMillis());
        }
    }
    
    @Override
    public String toString() {
        return String.format(
            "TaskMetrics[total=%d, running=%d, completed=%d, failed=%d, retried=%d, " +
            "avgTime=%dms, minTime=%dms, maxTime=%dms]",
            totalTasks.get(),
            runningTasks.get(),
            completedTasks.get(),
            failedTasks.get(),
            retriedTasks.get(),
            totalTasks.get() > 0 ? totalProcessingTimeMs.get() / totalTasks.get() : 0,
            minTaskTimeMs.get() == Long.MAX_VALUE ? 0 : minTaskTimeMs.get(),
            maxTaskTimeMs.get()
        );
    }
}
