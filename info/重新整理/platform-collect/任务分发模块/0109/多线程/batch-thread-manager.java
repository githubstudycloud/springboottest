import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class BatchThreadManager<T> {
    private final int totalSize;
    private final int batchSize;
    private final List<T> dataList;
    private final Consumer<List<T>> batchProcessor;
    private final AtomicInteger completedBatches = new AtomicInteger(0);
    private final AtomicLong startTime = new AtomicLong(0);
    private final AtomicLong endTime = new AtomicLong(0);
    private volatile int maxConcurrentThreads;
    private volatile boolean isVirtualThread;
    private volatile ExecutorService executorService;
    private final ConcurrentHashMap<String, ThreadTaskInfo> threadTaskInfoMap = new ConcurrentHashMap<>();

    public static class ThreadTaskInfo {
        private final long startTime;
        private volatile long endTime;
        private final int batchNumber;
        private final int batchSize;
        private volatile TaskStatus status;

        public ThreadTaskInfo(int batchNumber, int batchSize) {
            this.startTime = System.currentTimeMillis();
            this.batchNumber = batchNumber;
            this.batchSize = batchSize;
            this.status = TaskStatus.RUNNING;
        }

        public void complete() {
            this.endTime = System.currentTimeMillis();
            this.status = TaskStatus.COMPLETED;
        }

        public void fail() {
            this.endTime = System.currentTimeMillis();
            this.status = TaskStatus.FAILED;
        }

        @Override
        public String toString() {
            return String.format(
                "Batch %d (size: %d) - Status: %s, Duration: %dms",
                batchNumber, batchSize, status,
                (endTime > 0 ? endTime - startTime : System.currentTimeMillis() - startTime)
            );
        }
    }

    public enum TaskStatus {
        RUNNING, COMPLETED, FAILED
    }

    public BatchThreadManager(List<T> dataList, int batchSize, Consumer<List<T>> batchProcessor, 
                            int maxConcurrentThreads, boolean isVirtualThread) {
        this.dataList = dataList;
        this.totalSize = dataList.size();
        this.batchSize = batchSize;
        this.batchProcessor = batchProcessor;
        this.maxConcurrentThreads = maxConcurrentThreads;
        this.isVirtualThread = isVirtualThread;
        initializeExecutor();
    }

    private void initializeExecutor() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }

        if (isVirtualThread) {
            executorService = Executors.newVirtualThreadPerTaskExecutor();
        } else {
            executorService = new ThreadPoolExecutor(
                maxConcurrentThreads, maxConcurrentThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy()
            );
        }
    }

    public void updateThreadLimit(int newLimit) {
        this.maxConcurrentThreads = newLimit;
        if (!isVirtualThread) {
            initializeExecutor();
        }
    }

    public void switchThreadType(boolean useVirtualThread) {
        this.isVirtualThread = useVirtualThread;
        initializeExecutor();
    }

    public CompletableFuture<Void> executeBatches() {
        startTime.set(System.currentTimeMillis());
        int totalBatches = (totalSize + batchSize - 1) / batchSize;
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < totalSize; i += batchSize) {
            int batchNumber = i / batchSize;
            int start = i;
            int end = Math.min(i + batchSize, totalSize);
            
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                String threadName = Thread.currentThread().getName();
                ThreadTaskInfo taskInfo = new ThreadTaskInfo(batchNumber, end - start);
                threadTaskInfoMap.put(threadName, taskInfo);
                
                try {
                    List<T> batch = dataList.subList(start, end);
                    batchProcessor.accept(batch);
                    taskInfo.complete();
                    completedBatches.incrementAndGet();
                } catch (Exception e) {
                    taskInfo.fail();
                    throw new CompletionException(e);
                }
            }, executorService);
            
            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .whenComplete((v, e) -> {
                endTime.set(System.currentTimeMillis());
                executorService.shutdown();
            });
    }

    public double getProgress() {
        return (double) completedBatches.get() * batchSize / totalSize * 100;
    }

    public Duration getExecutionTime() {
        long end = endTime.get() > 0 ? endTime.get() : System.currentTimeMillis();
        return Duration.ofMillis(end - startTime.get());
    }

    public List<ThreadTaskInfo> getActiveTaskInfo() {
        return threadTaskInfoMap.values().stream()
            .filter(info -> info.status == TaskStatus.RUNNING)
            .toList();
    }

    public List<ThreadTaskInfo> getCompletedTaskInfo() {
        return threadTaskInfoMap.values().stream()
            .filter(info -> info.status == TaskStatus.COMPLETED)
            .toList();
    }

    public List<ThreadTaskInfo> getFailedTaskInfo() {
        return threadTaskInfoMap.values().stream()
            .filter(info -> info.status == TaskStatus.FAILED)
            .toList();
    }
}
