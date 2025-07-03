package io.github.ckaanf.ratelimiter.core.exceptions;

import java.time.Duration;

/**
 * Rate Limit 초과 시 발생하는 예외
 */
public class RateLimitExceededException extends RuntimeException {
    private final long tokensRequested;
    private final long availableTokens;
    private final Duration waitTime;

    public RateLimitExceededException(String message, long tokensRequested,
                                      long availableTokens, Duration waitTime) {
        super(message);
        this.tokensRequested = tokensRequested;
        this.availableTokens = availableTokens;
        this.waitTime = waitTime;
    }

    public RateLimitExceededException(long tokensRequested, long availableTokens, Duration waitTime) {
        this(String.format("Rate limit exceeded. Requested: %d, Available: %d, Wait time: %s",
                        tokensRequested, availableTokens, waitTime),
                tokensRequested, availableTokens, waitTime);
    }

    public long getTokensRequested() {
        return tokensRequested;
    }

    public long getAvailableTokens() {
        return availableTokens;
    }

    public Duration getWaitTime() {
        return waitTime;
    }
}
