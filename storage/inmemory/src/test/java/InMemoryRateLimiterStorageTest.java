
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

class InMemoryRateLimiterStorageTest {

    private InMemoryRateLimiterStorage storage;
    private RateLimiterConfig config;

    @BeforeEach
    void setUp() {
        MemoryStorageConfig storageConfig = new MemoryStorageConfig();
        storage = new InMemoryRateLimiterStorage(storageConfig);

        config = new RateLimiterConfig(
                "test-key",
                TokenBucketAlgorithmConfig.perSecond(10, 100),
                storageConfig
        );

        storage.initialize(config);
    }

    @Test
    @DisplayName("저장소 타입 반환")
    void shouldReturnCorrectStorageType() {
        assertThat(storage.getType()).isEqualTo("memory");
    }

    @Test
    @DisplayName("토큰 소비 성공")
    void shouldConsumeTokensSuccessfully() {
        // Given
        StorageContext context = StorageContext.forConsume("test-key", 10, config);

        // When
        StorageResult result = storage.tryConsume(context);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getConsumedTokens()).isEqualTo(10);
        assertThat(result.getAvailableTokens()).isEqualTo(90);
    }

    @Test
    @DisplayName("토큰 부족으로 소비 실패")
    void shouldFailWhenInsufficientTokens() {
        // Given
        StorageContext context = StorageContext.forConsume("test-key", 150, config);

        // When
        StorageResult result = storage.tryConsume(context);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getAvailableTokens()).isEqualTo(100);
        assertThat(result.getWaitTime()).isGreaterThan(Duration.ZERO);
    }

    @Test
    @DisplayName("연속 토큰 소비")
    void shouldHandleConsecutiveConsumptions() {
        // Given
        StorageContext context1 = StorageContext.forConsume("test-key", 30, config);
        StorageContext context2 = StorageContext.forConsume("test-key", 40, config);

        // When
        StorageResult result1 = storage.tryConsume(context1);
        StorageResult result2 = storage.tryConsume(context2);

        // Then
        assertThat(result1.isSuccess()).isTrue();
        assertThat(result1.getAvailableTokens()).isEqualTo(70);

        assertThat(result2.isSuccess()).isTrue();
        assertThat(result2.getAvailableTokens()).isEqualTo(30);
    }

    @Test
    @DisplayName("토큰 상태 조회")
    void shouldQueryTokenState() {
        // Given
        StorageContext consumeContext = StorageContext.forConsume("test-key", 25, config);
        storage.tryConsume(consumeContext);

        StorageContext queryContext = StorageContext.forQuery("test-key", config);

        // When
        StorageResult result = storage.getTokenState(queryContext);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getAvailableTokens()).isEqualTo(75);
    }

    @Test
    @DisplayName("존재하지 않는 키로 상태 조회")
    void shouldReturnInitialStateForNewKey() {
        // Given
        StorageContext context = StorageContext.forQuery("new-key", config);

        // When
        StorageResult result = storage.getTokenState(context);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getAvailableTokens()).isEqualTo(100); // 초기 토큰
    }

    @Test
    @DisplayName("다른 키들은 독립적으로 관리")
    void shouldManageKeysIndependently() {
        // Given
        StorageContext context1 = StorageContext.forConsume("key1", 30, config);
        StorageContext context2 = StorageContext.forConsume("key2", 50, config);
        StorageContext queryContext1 = StorageContext.forQuery("key1", config);
        StorageContext queryContext2 = StorageContext.forQuery("key2", config);

        // When
        storage.tryConsume(context1);
        storage.tryConsume(context2);

        StorageResult result1 = storage.getTokenState(queryContext1);
        StorageResult result2 = storage.getTokenState(queryContext2);

        // Then
        assertThat(result1.getAvailableTokens()).isEqualTo(70);
        assertThat(result2.getAvailableTokens()).isEqualTo(50);
    }

    @Test
    @DisplayName("저장소 정리")
    void shouldCleanupExpiredEntries() throws InterruptedException {
        // Given
        MemoryStorageConfig shortTtlConfig = MemoryStorageConfig.withTtl(Duration.ofMillis(50));
        InMemoryRateLimiterStorage shortTtlStorage = new InMemoryRateLimiterStorage(shortTtlConfig);

        RateLimiterConfig testConfig = new RateLimiterConfig(
                "expire-test", TokenBucketAlgorithmConfig.perSecond(10, 100), shortTtlConfig
        );
        shortTtlStorage.initialize(testConfig);

        StorageContext context = StorageContext.forConsume("expire-test", 10, testConfig);
        shortTtlStorage.tryConsume(context);

        // When
        Thread.sleep(100); // TTL 초과 대기
        shortTtlStorage.cleanup();

        // Then
        InMemoryRateLimiterStorage.MemoryStorageStats stats = shortTtlStorage.getStats();
        assertThat(stats.getActiveBuckets()).isEqualTo(0);
    }

    @Test
    @DisplayName("저장소 통계 조회")
    void shouldProvideStorageStats() {
        // Given
        StorageContext context1 = StorageContext.forConsume("stats1", 10, config);
        StorageContext context2 = StorageContext.forConsume("stats2", 20, config);

        // When
        storage.tryConsume(context1);
        storage.tryConsume(context2);

        InMemoryRateLimiterStorage.MemoryStorageStats stats = storage.getStats();

        // Then
        assertThat(stats.getActiveBuckets()).isEqualTo(2);
        assertThat(stats.getTotalRequests()).isEqualTo(30);
    }

    @Test
    @DisplayName("저장소 종료")
    void shouldShutdownGracefully() {
        // When & Then
        assertThatCode(() -> storage.shutdown()).doesNotThrowAnyException();
    }
}