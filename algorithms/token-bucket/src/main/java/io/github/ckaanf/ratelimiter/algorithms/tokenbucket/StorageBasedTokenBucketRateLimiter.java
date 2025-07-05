package io.github.ckaanf.ratelimiter.algorithms.tokenbucket;

import io.github.ckaanf.ratelimiter.core.*;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 저장소 기반 Token Bucket Rate Limiter
 * 순수하게 저장소에 의존하는 알고리즘 구현체
 */
public class StorageBasedTokenBucketRateLimiter implements RateLimiter {

    private final String key;
    private final RateLimiterConfig config;
    private final RateLimiterStorage storage;
    private final TokenBucketAlgorithmConfig algorithmConfig;

    public StorageBasedTokenBucketRateLimiter(String key, RateLimiterConfig config, RateLimiterStorage storage) {
        this.key = key;
        this.config = config;
        this.storage = storage;

        if (!(config.getAlgorithmConfig() instanceof TokenBucketAlgorithmConfig)) {
            throw new IllegalArgumentException("Invalid algorithm config type");
        }
        this.algorithmConfig = (TokenBucketAlgorithmConfig) config.getAlgorithmConfig();
    }

    @Override
    public boolean tryConsume(long tokens) {
        validateTokens(tokens);

        StorageContext context = StorageContext.forConsume(key, tokens, config);
        StorageResult result = storage.tryConsume(context);

        return result.isSuccess();
    }

    @Override
    public boolean tryConsume(long tokens, Duration maxWaitTime) {
        validateTokens(tokens);
        validateWaitTime(maxWaitTime);

        if (tryConsume(tokens)) {
            return true;
        }

        StorageContext queryContext = StorageContext.forQuery(key, config);
        StorageResult queryResult = storage.getTokenState(queryContext);

        if (queryResult.getWaitTime().compareTo(maxWaitTime) > 0) {
            return false;
        }

        try {
            Thread.sleep(queryResult.getWaitTime().toMillis());
            return tryConsume(tokens);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void consume(long tokens) throws InterruptedException {
        validateTokens(tokens);

        while (!tryConsume(tokens)) {
            StorageContext queryContext = StorageContext.forQuery(key, config);
            StorageResult queryResult = storage.getTokenState(queryContext);

            try {
                Thread.sleep(queryResult.getWaitTime().toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            }
        }
    }

    @Override
    public CompletableFuture<Boolean> consumeAsync(long tokens) {
        validateTokens(tokens);

        return CompletableFuture.supplyAsync(() -> {
            try {
                consume(tokens);
                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        });
    }

    @Override
    public long getAvailableTokens() {
        StorageContext context = StorageContext.forQuery(key, config);
        StorageResult result = storage.getTokenState(context);
        return result.getAvailableTokens();
    }

    @Override
    public RateLimiterConfig getConfiguration() {
        return config;
    }

    @Override
    public ConsumeResult tryConsumeWithResult(long tokens) {
        validateTokens(tokens);

        StorageContext context = StorageContext.forConsume(key, tokens, config);
        StorageResult result = storage.tryConsume(context);

        if (result.isSuccess()) {
            return ConsumeResult.consumed(tokens, result.getAvailableTokens(), result.getNextRefillTime());
        } else {
            return ConsumeResult.rejected(tokens, result.getAvailableTokens(),
                    result.getWaitTime(), result.getNextRefillTime());
        }
    }

    @Override
    public BucketState getBucketState() {
        StorageContext context = StorageContext.forQuery(key, config);
        StorageResult result = storage.getTokenState(context);

        Map<String, Object> metadata = result.getMetadata();

        return new BucketState(
                result.getAvailableTokens(),
                result.getNextRefillTime(),
                (Long) metadata.getOrDefault("totalConsumed", 0L),
                (Long) metadata.getOrDefault("totalRequested", 0L),
                (Long) metadata.getOrDefault("rejectedRequests", 0L)
        );
    }


    public boolean tryConsume() {
        return tryConsume(1);
    }

    public boolean tryConsume(Duration maxWaitTime) {
        return tryConsume(1, maxWaitTime);
    }

    public void consume() throws InterruptedException {
        consume(1);
    }

    public CompletableFuture<Boolean> consumeAsync() {
        return consumeAsync(1);
    }

    private void validateTokens(long tokens) {
        if (tokens <= 0) {
            throw new IllegalArgumentException("Tokens must be positive: " + tokens);
        }
    }

    private void validateWaitTime(Duration maxWaitTime) {
        if (maxWaitTime.isNegative()) {
            throw new IllegalArgumentException("Wait time cannot be negative: " + maxWaitTime);
        }
    }

    @Override
    public String toString() {
        return String.format("TokenBucketRateLimiter{key='%s', config=%s}", key, algorithmConfig);
    }
}