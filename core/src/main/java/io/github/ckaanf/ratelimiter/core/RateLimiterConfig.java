package io.github.ckaanf.ratelimiter.core;

import java.util.Map;
import java.util.Objects;

/**
 * Rate Limiter 통합 설정
 * 알고리즘과 저장소 설정을 조합하여 완전한 설정을 구성
 */
public class RateLimiterConfig {

    private final String key;
    private final AlgorithmConfig algorithmConfig;
    private final StorageConfig storageConfig;
    private final Map<String, Object> metadata;

    public RateLimiterConfig(String key,
                             AlgorithmConfig algorithmConfig,
                             StorageConfig storageConfig,
                             Map<String, Object> metadata) {
        this.key = Objects.requireNonNull(key, "Key cannot be null");
        this.algorithmConfig = Objects.requireNonNull(algorithmConfig, "Algorithm config cannot be null");
        this.storageConfig = Objects.requireNonNull(storageConfig, "Storage config cannot be null");
        this.metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public RateLimiterConfig(String key, AlgorithmConfig algorithmConfig, StorageConfig storageConfig) {
        this(key, algorithmConfig, storageConfig, null);
    }

    public String getKey() {
        return key;
    }

    public AlgorithmConfig getAlgorithmConfig() {
        return algorithmConfig;
    }

    public StorageConfig getStorageConfig() {
        return storageConfig;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public RateLimiterConfig withKey(String newKey) {
        return new RateLimiterConfig(newKey, algorithmConfig, storageConfig, metadata);
    }

    public RateLimiterConfig withMetadata(String key, Object value) {
        Map<String, Object> newMetadata = new java.util.HashMap<>(this.metadata);
        newMetadata.put(key, value);
        return new RateLimiterConfig(this.key, algorithmConfig, storageConfig, newMetadata);
    }

    @Override
    public String toString() {
        return String.format("io.github.ckaanf.ratelimiter.core.RateLimiterConfig{key='%s', algorithm=%s, storage=%s}",
                key, algorithmConfig.getType(), storageConfig.getType());
    }
}