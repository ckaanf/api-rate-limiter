import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class TokenBucketAlgorithmConfigTest {

    @Test
    @DisplayName("유효한 설정으로 생성")
    void shouldCreateWithValidParameters() {
        // When
        TokenBucketAlgorithmConfig config = new TokenBucketAlgorithmConfig(
                100, 10, Duration.ofSeconds(1), 50
        );

        // Then
        assertThat(config.getCapacity()).isEqualTo(100);
        assertThat(config.getRefillTokens()).isEqualTo(10);
        assertThat(config.getRefillPeriod()).isEqualTo(Duration.ofSeconds(1));
        assertThat(config.getInitialTokens()).isEqualTo(50);
        assertThat(config.getType()).isEqualTo("token-bucket");
    }

    @Test
    @DisplayName("초당 리필 팩토리 메소드")
    void shouldCreatePerSecondConfig() {
        // When
        TokenBucketAlgorithmConfig config = TokenBucketAlgorithmConfig.perSecond(100, 1000);

        // Then
        assertThat(config.getRefillTokens()).isEqualTo(100);
        assertThat(config.getCapacity()).isEqualTo(1000);
        assertThat(config.getRefillPeriod()).isEqualTo(Duration.ofSeconds(1));
        assertThat(config.getInitialTokens()).isEqualTo(1000);
    }

    @Test
    @DisplayName("분당 리필 팩토리 메소드")
    void shouldCreatePerMinuteConfig() {
        // When
        TokenBucketAlgorithmConfig config = TokenBucketAlgorithmConfig.perMinute(60, 500);

        // Then
        assertThat(config.getRefillTokens()).isEqualTo(60);
        assertThat(config.getCapacity()).isEqualTo(500);
        assertThat(config.getRefillPeriod()).isEqualTo(Duration.ofMinutes(1));
    }

    @Test
    @DisplayName("버스트 허용 설정")
    void shouldCreateBurstyConfig() {
        // When
        TokenBucketAlgorithmConfig config = TokenBucketAlgorithmConfig.bursty(50, 200);

        // Then
        assertThat(config.getRefillTokens()).isEqualTo(50);
        assertThat(config.getCapacity()).isEqualTo(200);
        assertThat(config.getInitialTokens()).isEqualTo(200); // 버스트 허용을 위해 full capacity
    }

    @Test
    @DisplayName("엄격한 제한 설정")
    void shouldCreateStrictConfig() {
        // When
        TokenBucketAlgorithmConfig config = TokenBucketAlgorithmConfig.strict(10);

        // Then
        assertThat(config.getCapacity()).isEqualTo(1);
        assertThat(config.getRefillTokens()).isEqualTo(10);
        assertThat(config.getInitialTokens()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    @DisplayName("잘못된 capacity로 생성 시 예외")
    void shouldRejectInvalidCapacity(long invalidCapacity) {
        assertThatThrownBy(() -> new TokenBucketAlgorithmConfig(
                invalidCapacity, 10, Duration.ofSeconds(1), 0
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Capacity must be positive");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -10})
    @DisplayName("잘못된 refillTokens로 생성 시 예외")
    void shouldRejectInvalidRefillTokens(long invalidRefillTokens) {
        assertThatThrownBy(() -> new TokenBucketAlgorithmConfig(
                100, invalidRefillTokens, Duration.ofSeconds(1), 0
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Refill tokens must be positive");
    }

    @Test
    @DisplayName("초기 토큰이 capacity를 초과할 때 예외")
    void shouldRejectInitialTokensExceedingCapacity() {
        assertThatThrownBy(() -> new TokenBucketAlgorithmConfig(
                100, 10, Duration.ofSeconds(1), 150
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Initial tokens cannot exceed capacity");
    }

    @Test
    @DisplayName("설정 검증")
    void shouldValidateConfiguration() {
        // Given
        TokenBucketAlgorithmConfig config = TokenBucketAlgorithmConfig.perSecond(100, 1000);

        // When & Then
        assertThatCode(config::validate).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Map 직렬화")
    void shouldSerializeToMap() {
        // Given
        TokenBucketAlgorithmConfig config = new TokenBucketAlgorithmConfig(
                100, 10, Duration.ofSeconds(1), 50
        );

        // When
        Map<String, Object> map = config.toMap();

        // Then
        assertThat(map).containsEntry("capacity", 100L);
        assertThat(map).containsEntry("refillTokens", 10L);
        assertThat(map).containsEntry("refillPeriodMs", 1000L);
        assertThat(map).containsEntry("initialTokens", 50L);
    }

    @Test
    @DisplayName("Map 역직렬화")
    void shouldDeserializeFromMap() {
        // Given
        Map<String, Object> map = Map.of(
                "capacity", 100L,
                "refillTokens", 10L,
                "refillPeriodMs", 1000L,
                "initialTokens", 50L
        );

        // When
        TokenBucketAlgorithmConfig config = TokenBucketAlgorithmConfig.fromMap(map);

        // Then
        assertThat(config.getCapacity()).isEqualTo(100);
        assertThat(config.getRefillTokens()).isEqualTo(10);
        assertThat(config.getRefillPeriod()).isEqualTo(Duration.ofSeconds(1));
        assertThat(config.getInitialTokens()).isEqualTo(50);
    }

    @Test
    @DisplayName("리필 속도 계산")
    void shouldCalculateRefillRate() {
        // Given
        TokenBucketAlgorithmConfig config = TokenBucketAlgorithmConfig.perSecond(100, 1000);

        // When
        double refillRate = config.getRefillRatePerSecond();

        // Then
        assertThat(refillRate).isEqualTo(100.0);
    }

    @Test
    @DisplayName("평등성 검증")
    void shouldImplementEqualsAndHashCode() {
        // Given
        TokenBucketAlgorithmConfig config1 = TokenBucketAlgorithmConfig.perSecond(100, 1000);
        TokenBucketAlgorithmConfig config2 = TokenBucketAlgorithmConfig.perSecond(100, 1000);
        TokenBucketAlgorithmConfig config3 = TokenBucketAlgorithmConfig.perSecond(200, 1000);

        // Then
        assertThat(config1).isEqualTo(config2);
        assertThat(config1).isNotEqualTo(config3);
        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
    }
}