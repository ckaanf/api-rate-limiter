package io.github.ckaanf.ratelimiter.inmemory;

import io.github.ckaanf.ratelimiter.algorithms.tokenbucket.TokenBucketAlgorithmConfig;
import io.github.ckaanf.ratelimiter.core.RateLimiterConfig;
import io.github.ckaanf.ratelimiter.core.RateLimiterStorage;
import io.github.ckaanf.ratelimiter.core.StorageContext;
import io.github.ckaanf.ratelimiter.core.StorageResult;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 메모리 기반 Rate Limiter 저장소
 * Thread-safe하며 TTL과 자동 정리 기능 포함
 */
public class InMemoryRateLimiterStorage implements RateLimiterStorage {

    private final MemoryStorageConfig config;
    private final ConcurrentHashMap<String, TokenBucketEntry> buckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> lastAccessTimes = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor;

    public InMemoryRateLimiterStorage(MemoryStorageConfig config) {
        this.config = config;
        this.cleanupExecutor = createCleanupExecutor();

        if (!config.getCleanupInterval().isZero()) {
            scheduleCleanup();
        }
    }

    public InMemoryRateLimiterStorage() {
        this(new MemoryStorageConfig());
    }

    @Override
    public String getType() {
        return "memory";
    }

    @Override
    public StorageResult tryConsume(StorageContext context) {
        String key = context.getKey();
        long tokensToConsume = context.getTokens();

        updateLastAccess(key);

        TokenBucketEntry entry = getOrCreateEntry(key, context.getConfig());

        // 리필 수행
        entry.refillIfNeeded();

        // 토큰 소비 시도
        return entry.tryConsume(tokensToConsume);
    }

    @Override
    public StorageResult getTokenState(StorageContext context) {
        String key = context.getKey();

        updateLastAccess(key);

        TokenBucketEntry entry = buckets.get(key);
        if (entry == null) {
            // 엔트리가 없으면 초기 상태 반환
            TokenBucketAlgorithmConfig algorithmConfig =
                    (TokenBucketAlgorithmConfig) context.getConfig().getAlgorithmConfig();
            return StorageResult.query(algorithmConfig.getInitialTokens(), Instant.now());
        }

        entry.refillIfNeeded();
        return entry.getState();
    }

    @Override
    public void initialize(RateLimiterConfig config) {
        // 메모리 저장소는 특별한 초기화 불필요
        // 설정 검증만 수행
        this.config.validate();
    }

    @Override
    public void cleanup() {
        if (config.getEntryTtl().isZero()) {
            return; // TTL이 0이면 정리하지 않음
        }

        Instant cutoff = Instant.now().minus(config.getEntryTtl());
        int removedCount = 0;

        for (Map.Entry<String, Instant> entry : lastAccessTimes.entrySet()) {
            if (entry.getValue().isBefore(cutoff)) {
                String key = entry.getKey();
                buckets.remove(key);
                lastAccessTimes.remove(key);
                removedCount++;
            }
        }

        // 메모리 사용량 제한 체크
        if (buckets.size() > config.getMaxEntries()) {
            cleanupOldestEntries();
        }

        if (removedCount > 0) {
            System.out.println("Cleaned up " + removedCount + " expired entries");
        }
    }

    @Override
    public void shutdown() {
        if (cleanupExecutor != null && !cleanupExecutor.isShutdown()) {
            cleanupExecutor.shutdown();
            try {
                if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    cleanupExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                cleanupExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // === 내부 메소드들 ===

    private TokenBucketEntry getOrCreateEntry(String key, RateLimiterConfig config) {
        return buckets.computeIfAbsent(key, k -> {
            TokenBucketAlgorithmConfig algorithmConfig =
                    (TokenBucketAlgorithmConfig) config.getAlgorithmConfig();
            return new TokenBucketEntry(algorithmConfig, this.config.isEnableMetrics());
        });
    }

    private void updateLastAccess(String key) {
        lastAccessTimes.put(key, Instant.now());
    }

    private ScheduledExecutorService createCleanupExecutor() {
        return Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "memory-storage-cleanup");
            thread.setDaemon(true);
            return thread;
        });
    }

    private void scheduleCleanup() {
        long intervalMs = config.getCleanupInterval().toMillis();
        cleanupExecutor.scheduleAtFixedRate(this::cleanup, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
    }

    private void cleanupOldestEntries() {
        // 가장 오래된 항목들을 제거하여 maxEntries 이하로 유지
        int targetSize = (int) (config.getMaxEntries() * 0.8); // 80%까지 줄임

        lastAccessTimes.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Instant>comparingByValue())
                .limit(buckets.size() - targetSize)
                .forEach(entry -> {
                    String key = entry.getKey();
                    buckets.remove(key);
                    lastAccessTimes.remove(key);
                });
    }

    /**
     * 저장소 통계 조회
     */
    public MemoryStorageStats getStats() {
        long totalRequests = buckets.values().stream()
                .mapToLong(entry -> entry.totalRequested.get())
                .sum();

        return new MemoryStorageStats(
                buckets.size(),
                lastAccessTimes.size(),
                totalRequests
        );
    }

    // === 내부 클래스들 ===

    /**
     * 토큰 버킷 엔트리 (Thread-safe)
     */
    private static class TokenBucketEntry {
        private final TokenBucketAlgorithmConfig config;
        private final boolean enableMetrics;
        private final ReentrantLock lock = new ReentrantLock();

        private final AtomicLong availableTokens;
        private final AtomicReference<Instant> lastRefillTime;
        private final AtomicLong totalConsumed = new AtomicLong(0);
        private final AtomicLong totalRequested = new AtomicLong(0);
        private final AtomicLong rejectedRequests = new AtomicLong(0);

        TokenBucketEntry(TokenBucketAlgorithmConfig config, boolean enableMetrics) {
            this.config = config;
            this.enableMetrics = enableMetrics;
            this.availableTokens = new AtomicLong(config.getInitialTokens());
            this.lastRefillTime = new AtomicReference<>(Instant.now());
        }

        StorageResult tryConsume(long tokens) {
            if (enableMetrics) {
                totalRequested.addAndGet(tokens);
            }

            while (true) {
                long current = availableTokens.get();

                if (current >= tokens) {
                    if (availableTokens.compareAndSet(current, current - tokens)) {
                        if (enableMetrics) {
                            totalConsumed.addAndGet(tokens);
                        }
                        return StorageResult.success(current - tokens, tokens);
                    }
                } else {
                    if (enableMetrics) {
                        rejectedRequests.incrementAndGet();
                    }
                    Duration waitTime = calculateWaitTime(tokens - current);
                    return StorageResult.failure(current, waitTime);
                }
            }
        }

        StorageResult getState() {
            Map<String, Object> metadata = new HashMap<>();
            if (enableMetrics) {
                metadata.put("totalConsumed", totalConsumed.get());
                metadata.put("totalRequested", totalRequested.get());
                metadata.put("rejectedRequests", rejectedRequests.get());
            }

            return StorageResult.query(availableTokens.get(), getNextRefillTime())
                    .withMetadata(metadata);
        }

        void refillIfNeeded() {
            Instant now = Instant.now();
            Instant lastRefill = lastRefillTime.get();

            Duration timeSinceRefill = Duration.between(lastRefill, now);
            if (timeSinceRefill.compareTo(config.getRefillPeriod()) >= 0) {
                lock.lock();
                try {
                    refillTokens(now);
                } finally {
                    lock.unlock();
                }
            }
        }

        private void refillTokens(Instant now) {
            Instant lastRefill = lastRefillTime.get();
            Duration timeSinceRefill = Duration.between(lastRefill, now);

            if (timeSinceRefill.compareTo(config.getRefillPeriod()) < 0) {
                return; // Double-checked locking
            }

            long refillCycles = timeSinceRefill.toMillis() / config.getRefillPeriod().toMillis();
            long tokensToAdd = refillCycles * config.getRefillTokens();

            if (tokensToAdd > 0) {
                while (true) {
                    long current = availableTokens.get();
                    long newTokens = Math.min(config.getCapacity(), current + tokensToAdd);

                    if (availableTokens.compareAndSet(current, newTokens)) {
                        break;
                    }
                }

                Instant newRefillTime = lastRefill.plus(config.getRefillPeriod().multipliedBy(refillCycles));
                lastRefillTime.set(newRefillTime);
            }
        }

        private Duration calculateWaitTime(long neededTokens) {
            long refillCycles = (neededTokens + config.getRefillTokens() - 1) / config.getRefillTokens();
            return config.getRefillPeriod().multipliedBy(refillCycles);
        }

        private Instant getNextRefillTime() {
            return lastRefillTime.get().plus(config.getRefillPeriod());
        }
    }

    /**
     * 메모리 저장소 통계
     */
    public static class MemoryStorageStats {
        private final int activeBuckets;
        private final int trackedKeys;
        private final long totalRequests;

        public MemoryStorageStats(int activeBuckets, int trackedKeys, long totalRequests) {
            this.activeBuckets = activeBuckets;
            this.trackedKeys = trackedKeys;
            this.totalRequests = totalRequests;
        }

        public int getActiveBuckets() { return activeBuckets; }
        public int getTrackedKeys() { return trackedKeys; }
        public long getTotalRequests() { return totalRequests; }

        @Override
        public String toString() {
            return String.format("MemoryStorage{buckets=%d, keys=%d, requests=%d}",
                    activeBuckets, trackedKeys, totalRequests);
        }
    }
}