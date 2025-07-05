package io.github.ckaanf.ratelimiter.example;

import io.github.ckaanf.ratelimiter.core.RateLimiter;
import io.github.ckaanf.ratelimiter.core.RateLimiterConfig;
import io.github.ckaanf.ratelimiter.core.RateLimiterRegistry;
import io.github.ckaanf.ratelimiter.algorithms.tokenbucket.TokenBucketAlgorithmConfig;
import io.github.ckaanf.ratelimiter.inmemory.MemoryStorageConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}