package com.study.collect.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RetryUtil {
    private static final Logger logger = LoggerFactory.getLogger(RetryUtil.class);

    public static <T> T retry(RetryCallback<T> action, RetryConfig config) {
        int attempts = 0;
        long delay = config.getInitialInterval();
        Exception lastException = null;

        while (attempts < config.getMaxAttempts()) {
            try {
                return action.doWithRetry();
            } catch (Exception e) {
                lastException = e;
                attempts++;
                if (attempts >= config.getMaxAttempts()) break;

                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }

                delay = calculateNextDelay(delay, config);
                logger.warn("Retry attempt {} failed, next delay: {}ms", attempts, delay, e);
            }
        }

        throw new RuntimeException("All retry attempts failed", lastException);
    }

    private static long calculateNextDelay(long currentDelay, RetryConfig config) {
        long nextDelay = (long) (currentDelay * config.getMultiplier());
        return Math.min(nextDelay, config.getMaxInterval());
    }

    @FunctionalInterface
    public interface RetryCallback<T> {
        T doWithRetry() throws Exception;
    }

    public static class RetryConfig {
        private final int maxAttempts;
        private final long initialInterval;
        private final long maxInterval;
        private final double multiplier;

        private RetryConfig(Builder builder) {
            this.maxAttempts = builder.maxAttempts;
            this.initialInterval = builder.initialInterval;
            this.maxInterval = builder.maxInterval;
            this.multiplier = builder.multiplier;
        }

        // Getters
        public int getMaxAttempts() {
            return maxAttempts;
        }

        public long getInitialInterval() {
            return initialInterval;
        }

        public long getMaxInterval() {
            return maxInterval;
        }

        public double getMultiplier() {
            return multiplier;
        }

        public static class Builder {
            private int maxAttempts = 3;
            private long initialInterval = 1000;
            private long maxInterval = 10000;
            private double multiplier = 2.0;

            public Builder maxAttempts(int maxAttempts) {
                this.maxAttempts = maxAttempts;
                return this;
            }

            public Builder initialInterval(long initialInterval) {
                this.initialInterval = initialInterval;
                return this;
            }

            public Builder maxInterval(long maxInterval) {
                this.maxInterval = maxInterval;
                return this;
            }

            public Builder multiplier(double multiplier) {
                this.multiplier = multiplier;
                return this;
            }

            public RetryConfig build() {
                return new RetryConfig(this);
            }
        }
    }
}