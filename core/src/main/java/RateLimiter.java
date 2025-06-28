import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface RateLimiter {
    /**
     * 토큰 소비 시도 (논블로킹)
     */
    boolean tryConsume(long tokens);

    /**
     * 최대 대기시간을 지정하여 토큰 소비 시도
     */
    boolean tryConsume(long tokens, Duration maxWaitTime);

    /**
     * 토큰 소비 (블로킹, 인터럽트 가능)
     */
    void consume(long tokens) throws InterruptedException;

    /**
     * 비동기 토큰 소비
     */
    CompletableFuture<Boolean> consumeAsync(long tokens);

    /**
     * 현재 사용 가능한 토큰 수
     */
    long getAvailableTokens();

    /**
     * Rate Limiter 설정 정보
     */
    RateLimiterConfiguration getConfiguration();
}