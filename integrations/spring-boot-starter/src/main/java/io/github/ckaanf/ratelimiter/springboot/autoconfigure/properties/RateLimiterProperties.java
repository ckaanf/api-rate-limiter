package io.github.ckaanf.ratelimiter.springboot.autoconfigure.properties;

import io.github.ckaanf.ratelimiter.core.AlgorithmConfig;
import io.github.ckaanf.ratelimiter.core.StorageConfig;
import io.github.ckaanf.ratelimiter.algorithms.tokenbucket.TokenBucketAlgorithmConfig;
import io.github.ckaanf.ratelimiter.inmemory.MemoryStorageConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Rate Limiter 설정 속성 클래스
 */
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterProperties {

    private boolean enabled = true;
    private String name = "defaultRateLimiter";
    private String key = "default";
    private AlgorithmConfig algorithmConfig = TokenBucketAlgorithmConfig.perSecond(10, 100);
    private StorageConfig storageConfig = new MemoryStorageConfig();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public AlgorithmConfig getAlgorithmConfig() {
        return algorithmConfig;
    }

    public void setAlgorithmConfig(AlgorithmConfig algorithmConfig) {
        this.algorithmConfig = algorithmConfig;
    }

    public StorageConfig getStorageConfig() {
        return storageConfig;
    }

    public void setStorageConfig(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }
} 