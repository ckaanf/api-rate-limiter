package io.github.ckaanf.ratelimiter.core;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * 저장소 작업 결과
 */
public class StorageResult {

    private final boolean success;
    private final long availableTokens;
    private final long consumedTokens;
    private final Duration waitTime;
    private final Instant nextRefillTime;
    private final Map<String, Object> metadata;

    private StorageResult(boolean success, long availableTokens, long consumedTokens,
                          Duration waitTime, Instant nextRefillTime, Map<String, Object> metadata) {
        this.success = success;
        this.availableTokens = availableTokens;
        this.consumedTokens = consumedTokens;
        this.waitTime = waitTime != null ? waitTime : Duration.ZERO;
        this.nextRefillTime = nextRefillTime;
        this.metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public static StorageResult success(long availableTokens, long consumedTokens) {
        return new StorageResult(true, availableTokens, consumedTokens, Duration.ZERO, null, null);
    }

    public static StorageResult failure(long availableTokens, Duration waitTime) {
        return new StorageResult(false, availableTokens, 0, waitTime, null, null);
    }

    public static StorageResult query(long availableTokens, Instant nextRefillTime) {
        return new StorageResult(true, availableTokens, 0, Duration.ZERO, nextRefillTime, null);
    }

    public StorageResult withMetadata(Map<String, Object> metadata) {
        return new StorageResult(success, availableTokens, consumedTokens, waitTime, nextRefillTime, metadata);
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public long getAvailableTokens() {
        return availableTokens;
    }

    public long getConsumedTokens() {
        return consumedTokens;
    }

    public Duration getWaitTime() {
        return waitTime;
    }

    public Instant getNextRefillTime() {
        return nextRefillTime;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}