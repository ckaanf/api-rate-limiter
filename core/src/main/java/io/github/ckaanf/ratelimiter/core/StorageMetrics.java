package io.github.ckaanf.ratelimiter.core;

/**
 * 저장소에서 관리하는 메트릭스 정보
 */
public class StorageMetrics {
    private final long totalRequested;
    private final long totalConsumed;
    private final long rejectedRequests;

    public StorageMetrics(long totalRequested, long totalConsumed, long rejectedRequests) {
        this.totalRequested = totalRequested;
        this.totalConsumed = totalConsumed;
        this.rejectedRequests = rejectedRequests;
    }

    public static StorageMetrics empty() {
        return new StorageMetrics(0, 0, 0);
    }

    public double getSuccessRate() {
        return totalRequested == 0 ? 0.0 : (double) totalConsumed / totalRequested;
    }

    // getters
    public long getTotalRequested() {
        return totalRequested;
    }

    public long getTotalConsumed() {
        return totalConsumed;
    }

    public long getRejectedRequests() {
        return rejectedRequests;
    }
}