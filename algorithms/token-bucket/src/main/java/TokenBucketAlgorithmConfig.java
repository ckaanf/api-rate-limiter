
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Token Bucket 알고리즘 설정
 * Core의 AlgorithmConfig 인터페이스 구현
 */
public final class TokenBucketAlgorithmConfig implements AlgorithmConfig {

    private final long capacity;
    private final long refillTokens;
    private final Duration refillPeriod;
    private final long initialTokens;

    public TokenBucketAlgorithmConfig(long capacity, long refillTokens,
                                      Duration refillPeriod, long initialTokens) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriod = refillPeriod;
        this.initialTokens = initialTokens;
        validate();
    }

    @Override
    public String getType() {
        return "token-bucket";
    }

    @Override
    public void validate() {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        if (refillTokens <= 0) {
            throw new IllegalArgumentException("Refill tokens must be positive");
        }
        if (refillPeriod.isNegative() || refillPeriod.isZero()) {
            throw new IllegalArgumentException("Refill period must be positive: " + refillPeriod);
        }
        if (initialTokens < 0) {
            throw new IllegalArgumentException("Initial tokens cannot be negative");
        }
        if (initialTokens > capacity) {
            throw new IllegalArgumentException("Initial tokens cannot exceed capacity");
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("capacity", capacity);
        map.put("refillTokens", refillTokens);
        map.put("refillPeriodMs", refillPeriod.toMillis());
        map.put("initialTokens", initialTokens);
        return map;
    }

    public static TokenBucketAlgorithmConfig fromMap(Map<String, Object> map) {
        return new TokenBucketAlgorithmConfig(
                ((Number) map.get("capacity")).longValue(),
                ((Number) map.get("refillTokens")).longValue(),
                Duration.ofMillis(((Number) map.get("refillPeriodMs")).longValue()),
                ((Number) map.get("initialTokens")).longValue()
        );
    }


    public static TokenBucketAlgorithmConfig perSecond(long rate, long capacity) {
        return new TokenBucketAlgorithmConfig(capacity, rate, Duration.ofSeconds(1), capacity);
    }

    public static TokenBucketAlgorithmConfig perMinute(long rate, long capacity) {
        return new TokenBucketAlgorithmConfig(capacity, rate, Duration.ofMinutes(1), capacity);
    }

    public static TokenBucketAlgorithmConfig perHour(long rate, long capacity) {
        return new TokenBucketAlgorithmConfig(capacity, rate, Duration.ofHours(1), capacity);
    }

    public static TokenBucketAlgorithmConfig strict(long rate) {
        return new TokenBucketAlgorithmConfig(1, rate, Duration.ofSeconds(1), 1);
    }

    public static TokenBucketAlgorithmConfig bursty(long rate, long burstCapacity) {
        return new TokenBucketAlgorithmConfig(burstCapacity, rate, Duration.ofSeconds(1), burstCapacity);
    }


    public long getCapacity() {
        return capacity;
    }

    public long getRefillTokens() {
        return refillTokens;
    }

    public Duration getRefillPeriod() {
        return refillPeriod;
    }

    public long getInitialTokens() {
        return initialTokens;
    }

    public double getRefillRatePerSecond() {
        return (double) refillTokens / refillPeriod.toMillis() * 1000.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenBucketAlgorithmConfig)) return false;
        TokenBucketAlgorithmConfig that = (TokenBucketAlgorithmConfig) o;
        return capacity == that.capacity &&
                refillTokens == that.refillTokens &&
                initialTokens == that.initialTokens &&
                Objects.equals(refillPeriod, that.refillPeriod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(capacity, refillTokens, refillPeriod, initialTokens);
    }

    @Override
    public String toString() {
        return String.format("TokenBucket{capacity=%d, rate=%.1f/s, initial=%d}",
                capacity, getRefillRatePerSecond(), initialTokens);
    }
}