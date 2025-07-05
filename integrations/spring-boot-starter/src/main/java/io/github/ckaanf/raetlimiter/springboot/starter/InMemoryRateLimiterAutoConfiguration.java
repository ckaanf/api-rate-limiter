package io.github.ckaanf.raetlimiter.springboot.starter;

import io.github.ckaanf.ratelimiter.algorithms.tokenbucket.TokenBucketAlgorithmConfig;
import io.github.ckaanf.ratelimiter.core.RateLimiter;
import io.github.ckaanf.ratelimiter.core.RateLimiterConfig;
import io.github.ckaanf.ratelimiter.core.RateLimiterRegistry;
import io.github.ckaanf.ratelimiter.inmemory.MemoryStorageConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(RateLimiterProperties.class)
@ConditionalOnProperty(prefix = "ratelimiter", name = "enabled", havingValue = "true", matchIfMissing = true)
public class InMemoryRateLimiterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RateLimiter inMemoryRateLimiter(RateLimiterProperties properties) {

        TokenBucketAlgorithmConfig algorithmConfig = new TokenBucketAlgorithmConfig(
                properties.getCapacity(),
                properties.getRefillTokens(),
                Duration.ofSeconds(properties.getRefillPeriod()),
                properties.getInitialTokens()
        );

        MemoryStorageConfig storageConfig = new MemoryStorageConfig();

        RateLimiterConfig config = new RateLimiterConfig(
                properties.getKey(),
                algorithmConfig,
                storageConfig
        );

        return RateLimiterRegistry.getInstance().getRateLimiter(config);
    }
}
