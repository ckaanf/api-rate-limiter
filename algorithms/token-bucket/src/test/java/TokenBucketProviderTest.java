import io.github.ckaanf.ratelimiter.algorithms.tokenbucket.StorageBasedTokenBucketRateLimiter;
import io.github.ckaanf.ratelimiter.algorithms.tokenbucket.TokenBucketAlgorithmConfig;
import io.github.ckaanf.ratelimiter.algorithms.tokenbucket.TokenBucketProvider;
import io.github.ckaanf.ratelimiter.core.AlgorithmConfig;
import io.github.ckaanf.ratelimiter.core.RateLimiter;
import io.github.ckaanf.ratelimiter.core.RateLimiterConfig;
import io.github.ckaanf.ratelimiter.core.RateLimiterStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.*;

class TokenBucketProviderTest {

    private final TokenBucketProvider provider = new TokenBucketProvider();

    @Test
    @DisplayName("알고리즘 타입 반환")
    void shouldReturnCorrectAlgorithmType() {
        assertThat(provider.getAlgorithmType()).isEqualTo("token-bucket");
    }

    @Test
    @DisplayName("io.github.ckaanf.ratelimiter.algorithms.tokenbucket.TokenBucketAlgorithmConfig 지원 확인")
    void shouldSupportTokenBucketConfig() {
        // Given
        RateLimiterConfig config = Mockito.mock(RateLimiterConfig.class);
        TokenBucketAlgorithmConfig algorithmConfig = TokenBucketAlgorithmConfig.perSecond(100, 1000);

        Mockito.when(config.getAlgorithmConfig()).thenReturn(algorithmConfig);

        // When & Then
        assertThat(provider.supports(config)).isTrue();
    }

    @Test
    @DisplayName("다른 알고리즘 설정 지원하지 않음")
    void shouldNotSupportOtherConfigs() {
        // Given
        RateLimiterConfig config = Mockito.mock(RateLimiterConfig.class);
        AlgorithmConfig otherConfig = Mockito.mock(AlgorithmConfig.class);

        Mockito.when(config.getAlgorithmConfig()).thenReturn(otherConfig);

        // When & Then
        assertThat(provider.supports(config)).isFalse();
    }

    @Test
    @DisplayName("Rate Limiter 생성")
    void shouldCreateRateLimiter() {
        // Given
        RateLimiterConfig config = Mockito.mock(RateLimiterConfig.class);
        RateLimiterStorage storage = Mockito.mock(RateLimiterStorage.class);

        Mockito.when(config.getKey()).thenReturn("test-key");
        Mockito.when(config.getAlgorithmConfig())
                .thenReturn(TokenBucketAlgorithmConfig.perSecond(100, 1000));

        // When
        RateLimiter limiter = provider.create(config, storage);

        // Then
        assertThat(limiter).isNotNull();
        assertThat(limiter).isInstanceOf(StorageBasedTokenBucketRateLimiter.class);
    }

    @Test
    @DisplayName("높은 우선순위 확인")
    void shouldHaveHighPriority() {
        assertThat(provider.getPriority()).isEqualTo(100);
    }
}