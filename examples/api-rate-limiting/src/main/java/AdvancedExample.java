import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 고급 Rate Limiter 사용 예제
 */
public class AdvancedExample {

    public static void main(String[] args) throws Exception {
        demonstrateFactoryMethods();
        demonstrateAsyncUsage();
        demonstrateCustomConfiguration();
    }

    /**
     * 다양한 팩토리 메소드 사용법
     */
    private static void demonstrateFactoryMethods() {
        System.out.println("=== 팩토리 메소드 예제 ===");

        // 1. 엄격한 제한 (1개 버킷, 초당 10개 리필)
        RateLimiter strictLimiter = createRateLimiter(
                "strict-api",
                TokenBucketAlgorithmConfig.strict(10)
        );
        System.out.println("엄격한 제한: " + strictLimiter.getConfiguration());

        // 2. 버스트 허용 (200개 버킷, 초당 50개 리필)
        RateLimiter burstyLimiter = createRateLimiter(
                "bursty-api",
                TokenBucketAlgorithmConfig.bursty(50, 200)
        );
        System.out.println("버스트 허용: " + burstyLimiter.getConfiguration());

        // 3. 분당 제한
        RateLimiter perMinuteLimiter = createRateLimiter(
                "minute-api",
                TokenBucketAlgorithmConfig.perMinute(60, 500)
        );
        System.out.println("분당 제한: " + perMinuteLimiter.getConfiguration());

        System.out.println();
    }

    /**
     * 비동기 사용법
     */
    private static void demonstrateAsyncUsage() throws Exception {
        System.out.println("=== 비동기 사용 예제 ===");

        RateLimiter limiter = createRateLimiter(
                "async-api",
                TokenBucketAlgorithmConfig.perSecond(5, 10)
        );

        ExecutorService executor = Executors.newFixedThreadPool(3);

        // 여러 비동기 요청 실행
        CompletableFuture<Boolean>[] futures = new CompletableFuture[10];
        for (int i = 0; i < 10; i++) {
            final int requestId = i + 1;
            futures[i] = limiter.consumeAsync(1)
                    .thenApply(success -> {
                        System.out.println("비동기 요청 " + requestId + ": " +
                                (success ? "성공" : "실패") +
                                " (남은 토큰: " + limiter.getAvailableTokens() + ")");
                        return success;
                    });
        }

        // 모든 요청 완료 대기
        CompletableFuture.allOf(futures).join();

        executor.shutdown();
        System.out.println();
    }

    /**
     * 커스텀 설정 예제
     */
    private static void demonstrateCustomConfiguration() {
        System.out.println("=== 커스텀 설정 예제 ===");

        // 커스텀 메모리 저장소 설정
        MemoryStorageConfig customStorage = new MemoryStorageConfig(
                Duration.ofMinutes(30),  // 30분 TTL
                Duration.ofMinutes(5),   // 5분마다 정리
                5000,                    // 최대 5000개 엔트리
                true                     // 메트릭 활성화
        );

        // 커스텀 알고리즘 설정
        TokenBucketAlgorithmConfig customAlgorithm = new TokenBucketAlgorithmConfig(
                1000,                    // 1000개 용량
                50,                      // 50개씩 리필
                Duration.ofSeconds(1),   // 1초마다
                500                      // 초기 500개
        );

        RateLimiterConfig config = new RateLimiterConfig(
                "custom-api",
                customAlgorithm,
                customStorage
        );

        RateLimiter limiter = RateLimiterRegistry.getInstance().getRateLimiter(config);

        System.out.println("커스텀 설정으로 생성된 Rate Limiter:");
        System.out.println("- 초기 토큰: " + limiter.getAvailableTokens());
        System.out.println("- 설정: " + limiter.getConfiguration());
        System.out.println();
    }

    private static RateLimiter createRateLimiter(String key, TokenBucketAlgorithmConfig algorithm) {
        RateLimiterConfig config = new RateLimiterConfig(
                key,
                algorithm,
                new MemoryStorageConfig()
        );
        return RateLimiterRegistry.getInstance().getRateLimiter(config);
    }
}
