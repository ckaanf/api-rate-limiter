import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Rate Limiter 핵심 인터페이스
 *
 * <p>토큰 기반 rate limiting을 제공하며, 다양한 소비 패턴을 지원합니다:
 * <ul>
 *   <li>논블로킹 소비: {@link #tryConsume(long)}</li>
 *   <li>제한된 대기시간: {@link #tryConsume(long, Duration)}</li>
 *   <li>블로킹 소비: {@link #consume(long)}</li>
 *   <li>비동기 소비: {@link #consumeAsync(long)}</li>
 * </ul>
 *
 * <p>사용 예제:
 * <pre>{@code
 * // 설정 생성
 * RateLimiterConfig config = new RateLimiterConfig(
 *     "api-user-123",
 *     TokenBucketAlgorithmConfig.perSecond(100, 10),
 *     new MemoryStorageConfig()
 * );
 *
 * // Rate Limiter 생성
 * RateLimiter limiter = RateLimiterRegistry.getInstance().getRateLimiter(config);
 *
 * if (limiter.tryConsume(1)) {
 *     // API 호출 허용
 *     processRequest();
 * } else {
 *     // Rate limit 초과
 *     handleRateLimit();
 * }
 * }</pre>
 */
public interface RateLimiter {

    /**
     * 토큰 소비 시도 (논블로킹)
     *
     * @param tokens 소비할 토큰 수 (양수)
     * @return 소비 성공 여부
     * @throws IllegalArgumentException tokens가 0 이하인 경우
     */
    boolean tryConsume(long tokens);

    /**
     * 최대 대기시간을 지정하여 토큰 소비 시도
     *
     * @param tokens 소비할 토큰 수 (양수)
     * @param maxWaitTime 최대 대기시간 (음수 불가)
     * @return 소비 성공 여부
     * @throws IllegalArgumentException tokens가 0 이하이거나 maxWaitTime이 음수인 경우
     */
    boolean tryConsume(long tokens, Duration maxWaitTime);

    /**
     * 토큰 소비 (블로킹, 인터럽트 가능)
     *
     * @param tokens 소비할 토큰 수 (양수)
     * @throws InterruptedException 대기 중 인터럽트된 경우
     * @throws IllegalArgumentException tokens가 0 이하인 경우
     */
    void consume(long tokens) throws InterruptedException;

    /**
     * 비동기 토큰 소비
     *
     * @param tokens 소비할 토큰 수 (양수)
     * @return 소비 성공 여부를 나타내는 CompletableFuture
     * @throws IllegalArgumentException tokens가 0 이하인 경우
     */
    CompletableFuture<Boolean> consumeAsync(long tokens);

    /**
     * 현재 사용 가능한 토큰 수
     *
     * @return 사용 가능한 토큰 수
     */
    long getAvailableTokens();

    /**
     * Rate Limiter 설정 정보
     *
     * @return 설정 정보
     */
    RateLimiterConfig getConfiguration();

    /**
     * 상세한 토큰 소비 결과 반환
     *
     * @param tokens 소비할 토큰 수 (양수)
     * @return 소비 결과 상세 정보
     * @throws IllegalArgumentException tokens가 0 이하인 경우
     */
    default ConsumeResult tryConsumeWithResult(long tokens) {
        if (tokens <= 0) {
            throw new IllegalArgumentException("Tokens must be positive");
        }

        boolean consumed = tryConsume(tokens);
        long remaining = getAvailableTokens();

        if (consumed) {
            return ConsumeResult.consumed(tokens, remaining, null);
        } else {
            return ConsumeResult.rejected(tokens, remaining, Duration.ZERO, null);
        }
    }

    /**
     * 현재 버킷 상태 조회
     *
     * @return 버킷 상태 정보
     */
    default BucketState getBucketState() {
        long available = getAvailableTokens();
        return new BucketState(available, null, 0, 0, 0);
    }

    // === 편의 메소드들 (단일 토큰 소비) ===

    /**
     * 단일 토큰 소비 시도 (논블로킹)
     */
    default boolean tryConsume() {
        return tryConsume(1);
    }

    /**
     * 최대 대기시간을 지정하여 단일 토큰 소비 시도
     */
    default boolean tryConsume(Duration maxWaitTime) {
        return tryConsume(1, maxWaitTime);
    }

    /**
     * 단일 토큰 소비 (블로킹)
     */
    default void consume() throws InterruptedException {
        consume(1);
    }

    /**
     * 비동기 단일 토큰 소비
     */
    default CompletableFuture<Boolean> consumeAsync() {
        return consumeAsync(1);
    }
}