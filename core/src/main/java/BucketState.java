
import java.time.Instant;

/**
 * Rate Limiter 버킷의 현재 상태
 */
public class BucketState {

    private final long availableTokens;
    private final Instant lastRefillTime;
    private final long totalConsumed;
    private final long totalRequested;
    private final long rejectedRequests;

    public BucketState(long availableTokens, Instant lastRefillTime,
                       long totalConsumed, long totalRequested, long rejectedRequests) {
        this.availableTokens = availableTokens;
        this.lastRefillTime = lastRefillTime;
        this.totalConsumed = totalConsumed;
        this.totalRequested = totalRequested;
        this.rejectedRequests = rejectedRequests;
    }

    public long getAvailableTokens() {
        return availableTokens;
    }

    public Instant getLastRefillTime() {
        return lastRefillTime;
    }

    public long getTotalConsumed() {
        return totalConsumed;
    }

    public long getTotalRequested() {
        return totalRequested;
    }

    public long getRejectedRequests() {
        return rejectedRequests;
    }

    /**
     * 성공률 계산 (요청이 없으면 0.0 반환)
     */
    public double getSuccessRate() {
        return totalRequested == 0 ? 0.0 : (double) totalConsumed / totalRequested;
    }

    /**
     * 거부율 계산
     */
    public double getRejectionRate() {
        return totalRequested == 0 ? 0.0 : (double) rejectedRequests / totalRequested;
    }

    /**
     * 토큰 사용률 (capacity 대비 사용된 비율)
     */
    public double getUtilizationRate(long capacity) {
        return capacity == 0 ? 0.0 : (double) (capacity - availableTokens) / capacity;
    }

    /**
     * 빈 상태 여부 (토큰이 없음)
     */
    public boolean isEmpty() {
        return availableTokens == 0;
    }

    /**
     * 사용된 적이 있는지 여부
     */
    public boolean hasBeenUsed() {
        return totalRequested > 0;
    }

    @Override
    public String toString() {
        return String.format("BucketState{available=%d, consumed=%d, requested=%d, rejected=%d, successRate=%.2f%%}",
                availableTokens, totalConsumed, totalRequested, rejectedRequests, getSuccessRate() * 100);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BucketState that = (BucketState) obj;
        return availableTokens == that.availableTokens &&
                totalConsumed == that.totalConsumed &&
                totalRequested == that.totalRequested &&
                rejectedRequests == that.rejectedRequests &&
                java.util.Objects.equals(lastRefillTime, that.lastRefillTime);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(availableTokens, lastRefillTime, totalConsumed, totalRequested, rejectedRequests);
    }
}