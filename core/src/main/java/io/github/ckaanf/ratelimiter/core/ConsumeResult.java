package io.github.ckaanf.ratelimiter.core;

import java.time.Duration;
import java.time.Instant;

/**
 * 토큰 소비 결과
 */
public class ConsumeResult {

    private final boolean consumed;
    private final long requestedTokens;
    private final long remainingTokens;
    private final Duration waitTime;
    private final Instant nextRefillTime;
    private final String reason;

    private ConsumeResult(boolean consumed, long requestedTokens, long remainingTokens,
                          Duration waitTime, Instant nextRefillTime, String reason) {
        this.consumed = consumed;
        this.requestedTokens = requestedTokens;
        this.remainingTokens = remainingTokens;
        this.waitTime = waitTime != null ? waitTime : Duration.ZERO;
        this.nextRefillTime = nextRefillTime;
        this.reason = reason;
    }

    /**
     * 성공한 소비 결과 생성
     */
    public static ConsumeResult consumed(long requestedTokens, long remainingTokens, Instant nextRefillTime) {
        return new ConsumeResult(true, requestedTokens, remainingTokens, Duration.ZERO, nextRefillTime, null);
    }

    /**
     * 거부된 소비 결과 생성
     */
    public static ConsumeResult rejected(long requestedTokens, long remainingTokens,
                                         Duration waitTime, Instant nextRefillTime) {
        return new ConsumeResult(false, requestedTokens, remainingTokens, waitTime, nextRefillTime, null);
    }

    /**
     * 사유와 함께 거부된 소비 결과 생성
     */
    public static ConsumeResult rejected(long requestedTokens, long remainingTokens,
                                         Duration waitTime, Instant nextRefillTime, String reason) {
        return new ConsumeResult(false, requestedTokens, remainingTokens, waitTime, nextRefillTime, reason);
    }

    // === Getters ===

    public boolean isConsumed() {
        return consumed;
    }

    public long getRequestedTokens() {
        return requestedTokens;
    }

    public long getRemainingTokens() {
        return remainingTokens;
    }

    public Duration getWaitTime() {
        return waitTime;
    }

    public Instant getNextRefillTime() {
        return nextRefillTime;
    }

    public String getReason() {
        return reason;
    }

    // === 편의 메소드들 ===

    /**
     * 소비 실패 여부
     */
    public boolean isRejected() {
        return !consumed;
    }

    /**
     * 즉시 재시도 가능 여부 (대기 시간이 0)
     */
    public boolean canRetryImmediately() {
        return waitTime.isZero();
    }

    /**
     * 대기 필요 여부
     */
    public boolean requiresWait() {
        return !waitTime.isZero();
    }

    @Override
    public String toString() {
        if (consumed) {
            return String.format("io.github.ckaanf.ratelimiter.core.ConsumeResult{consumed=true, requested=%d, remaining=%d, nextRefill=%s}",
                    requestedTokens, remainingTokens, nextRefillTime);
        } else {
            return String.format("io.github.ckaanf.ratelimiter.core.ConsumeResult{consumed=false, requested=%d, remaining=%d, waitTime=%s, reason='%s'}",
                    requestedTokens, remainingTokens, waitTime, reason);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ConsumeResult that = (ConsumeResult) obj;
        return consumed == that.consumed &&
                requestedTokens == that.requestedTokens &&
                remainingTokens == that.remainingTokens &&
                waitTime.equals(that.waitTime) &&
                java.util.Objects.equals(nextRefillTime, that.nextRefillTime) &&
                java.util.Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(consumed, requestedTokens, remainingTokens, waitTime, nextRefillTime, reason);
    }
}