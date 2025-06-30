import java.time.Duration;

/**
 * 기본 Rate Limiter 사용 예제
 */
public class BasicExample {

    public static void main(String[] args) throws InterruptedException {
        // 1. 설정 생성 - 초당 100개 토큰, 최대 1000개 버킷
        TokenBucketAlgorithmConfig algorithmConfig = TokenBucketAlgorithmConfig.perSecond(100, 1000);
        MemoryStorageConfig storageConfig = new MemoryStorageConfig();

        RateLimiterConfig config = new RateLimiterConfig(
                "api-user-123",
                algorithmConfig,
                storageConfig
        );

        // 2. Rate Limiter 생성
        RateLimiter limiter = RateLimiterRegistry.getInstance().getRateLimiter(config);

        // 3. 기본 사용법
        System.out.println("=== 기본 토큰 소비 테스트 ===");
        for (int i = 0; i < 5; i++) {
            if (limiter.tryConsume()) {
                System.out.println("요청 " + (i + 1) + ": 허용됨 (남은 토큰: " + limiter.getAvailableTokens() + ")");
            } else {
                System.out.println("요청 " + (i + 1) + ": 거부됨 (남은 토큰: " + limiter.getAvailableTokens() + ")");
            }
        }

        // 4. 대량 토큰 소비
        System.out.println("\n=== 대량 토큰 소비 테스트 ===");
        long tokensToConsume = 50;
        if (limiter.tryConsume(tokensToConsume)) {
            System.out.println(tokensToConsume + "개 토큰 소비 성공 (남은 토큰: " + limiter.getAvailableTokens() + ")");
        } else {
            System.out.println(tokensToConsume + "개 토큰 소비 실패 (남은 토큰: " + limiter.getAvailableTokens() + ")");
        }

        // 5. 대기 시간 지정 소비
        System.out.println("\n=== 대기 시간 지정 소비 테스트 ===");
        boolean result = limiter.tryConsume(10, Duration.ofMillis(100));
        System.out.println("100ms 대기 후 10개 토큰 소비: " + (result ? "성공" : "실패"));

        // 6. 상세 결과 조회
        System.out.println("\n=== 상세 결과 조회 ===");
        ConsumeResult detailResult = limiter.tryConsumeWithResult(5);
        System.out.println("소비 결과: " + detailResult);

        // 7. 버킷 상태 조회
        BucketState state = limiter.getBucketState();
        System.out.println("현재 버킷 상태: " + state);
    }
}
