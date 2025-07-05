package io.github.ckaanf.ratelimiter.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class RateLimiterConfigTest {

    @Test
    @DisplayName("유효한 설정으로 io.github.ckaanf.ratelimiter.core.RateLimiterConfig 생성")
    void shouldCreateValidConfig() {
        // Given
        TestAlgorithmConfig algorithmConfig = new TestAlgorithmConfig();
        TestStorageConfig storageConfig = new TestStorageConfig();

        // When
        RateLimiterConfig config = new RateLimiterConfig(
                "test-key", algorithmConfig, storageConfig
        );

        // Then
        assertThat(config.getKey()).isEqualTo("test-key");
        assertThat(config.getAlgorithmConfig()).isEqualTo(algorithmConfig);
        assertThat(config.getStorageConfig()).isEqualTo(storageConfig);
        assertThat(config.getMetadata()).isEmpty();
    }

    @Test
    @DisplayName("메타데이터와 함께 설정 생성")
    void shouldCreateConfigWithMetadata() {
        // Given
        TestAlgorithmConfig algorithmConfig = new TestAlgorithmConfig();
        TestStorageConfig storageConfig = new TestStorageConfig();
        Map<String, Object> metadata = Map.of("env", "test", "version", "1.0");

        // When
        RateLimiterConfig config = new RateLimiterConfig(
                "test-key", algorithmConfig, storageConfig, metadata
        );

        // Then
        assertThat(config.getMetadata()).containsAllEntriesOf(metadata);
    }

    @Test
    @DisplayName("새로운 키로 설정 복사")
    void shouldCreateConfigWithNewKey() {
        // Given
        TestAlgorithmConfig algorithmConfig = new TestAlgorithmConfig();
        TestStorageConfig storageConfig = new TestStorageConfig();
        RateLimiterConfig original = new RateLimiterConfig(
                "original-key", algorithmConfig, storageConfig
        );

        // When
        RateLimiterConfig newConfig = original.withKey("new-key");

        // Then
        assertThat(newConfig.getKey()).isEqualTo("new-key");
        assertThat(newConfig.getAlgorithmConfig()).isEqualTo(algorithmConfig);
        assertThat(newConfig.getStorageConfig()).isEqualTo(storageConfig);
    }

    @Test
    @DisplayName("메타데이터 추가")
    void shouldAddMetadata() {
        // Given
        TestAlgorithmConfig algorithmConfig = new TestAlgorithmConfig();
        TestStorageConfig storageConfig = new TestStorageConfig();
        RateLimiterConfig original = new RateLimiterConfig(
                "test-key", algorithmConfig, storageConfig
        );

        // When
        RateLimiterConfig withMetadata = original.withMetadata("env", "production");

        // Then
        assertThat(withMetadata.getMetadata()).containsEntry("env", "production");
        assertThat(original.getMetadata()).isEmpty(); // 원본은 변경되지 않음
    }

    @Test
    @DisplayName("toString 출력 확인")
    void shouldProvideReadableString() {
        // Given
        TestAlgorithmConfig algorithmConfig = new TestAlgorithmConfig();
        TestStorageConfig storageConfig = new TestStorageConfig();
        RateLimiterConfig config = new RateLimiterConfig(
                "test-key", algorithmConfig, storageConfig
        );

        // When
        String result = config.toString();

        // Then
        assertThat(result)
                .contains("test-key")
                .contains("test-algorithm")
                .contains("test-storage");
    }

    @Test
    @DisplayName("null 파라미터로 생성 시 예외 발생")
    void shouldThrowExceptionForNullParameters() {
        TestAlgorithmConfig algorithmConfig = new TestAlgorithmConfig();
        TestStorageConfig storageConfig = new TestStorageConfig();

        assertThatThrownBy(() -> new RateLimiterConfig(null, algorithmConfig, storageConfig))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Key cannot be null");

        assertThatThrownBy(() -> new RateLimiterConfig("key", null, storageConfig))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Algorithm config cannot be null");

        assertThatThrownBy(() -> new RateLimiterConfig("key", algorithmConfig, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Storage config cannot be null");
    }

    // === 테스트용 Helper 클래스들 ===

    private static class TestAlgorithmConfig implements AlgorithmConfig {
        @Override
        public String getType() { return "test-algorithm"; }

        @Override
        public void validate() {}

        @Override
        public Map<String, Object> toMap() {
            return Map.of("type", "test-algorithm");
        }
    }

    private static class TestStorageConfig implements StorageConfig {
        @Override
        public String getType() { return "test-storage"; }

        @Override
        public void validate() {}

        @Override
        public Map<String, Object> getConnectionProperties() {
            return Map.of();
        }

        @Override
        public Map<String, Object> getPerformanceProperties() {
            return Map.of("type", "test-storage");
        }
    }
}