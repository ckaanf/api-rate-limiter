import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 메모리 저장소 설정
 */
public final class MemoryStorageConfig implements StorageConfig {

    private final Duration entryTtl;
    private final Duration cleanupInterval;
    private final int maxEntries;
    private final boolean enableMetrics;

    public MemoryStorageConfig(Duration entryTtl, Duration cleanupInterval,
                               int maxEntries, boolean enableMetrics) {
        // 생성자에서 유효성 검증 수행
        if (entryTtl.isNegative()) {
            throw new IllegalArgumentException("Entry TTL cannot be negative");
        }
        if (cleanupInterval.isNegative()) {
            throw new IllegalArgumentException("Cleanup interval cannot be negative");
        }
        if (maxEntries <= 0) {
            throw new IllegalArgumentException("Max entries must be positive");
        }

        this.entryTtl = entryTtl;
        this.cleanupInterval = cleanupInterval;
        this.maxEntries = maxEntries;
        this.enableMetrics = enableMetrics;
    }

    public MemoryStorageConfig() {
        this(Duration.ofHours(1), Duration.ofMinutes(10), 10000, true);
    }

    @Override
    public String getType() {
        return "memory";
    }

    @Override
    public void validate() {
        if (entryTtl.isNegative()) {
            throw new IllegalArgumentException("Entry TTL cannot be negative");
        }
        if (cleanupInterval.isNegative()) {
            throw new IllegalArgumentException("Cleanup interval cannot be negative");
        }
        if (maxEntries <= 0) {
            throw new IllegalArgumentException("Max entries must be positive");
        }
    }

    @Override
    public Map<String, Object> getConnectionProperties() {
        return Map.of(); // 메모리 저장소는 연결 설정 없음
    }

    @Override
    public Map<String, Object> getPerformanceProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("entryTtlMs", entryTtl.toMillis());
        props.put("cleanupIntervalMs", cleanupInterval.toMillis());
        props.put("maxEntries", maxEntries);
        props.put("enableMetrics", enableMetrics);
        return props;
    }

    // === Factory methods ===

    public static MemoryStorageConfig defaultConfig() {
        return new MemoryStorageConfig();
    }

    public static MemoryStorageConfig withTtl(Duration ttl) {
        return new MemoryStorageConfig(ttl, Duration.ofMinutes(10), 10000, true);
    }

    public static MemoryStorageConfig unlimited() {
        return new MemoryStorageConfig(Duration.ofDays(365), Duration.ZERO, Integer.MAX_VALUE, false);
    }

    // === Getters ===

    public Duration getEntryTtl() { return entryTtl; }
    public Duration getCleanupInterval() { return cleanupInterval; }
    public int getMaxEntries() { return maxEntries; }
    public boolean isEnableMetrics() { return enableMetrics; }

    @Override
    public String toString() {
        return String.format("MemoryStorage{ttl=%s, maxEntries=%d}", entryTtl, maxEntries);
    }
}