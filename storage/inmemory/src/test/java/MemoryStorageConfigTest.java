import io.github.ckaanf.ratelimiter.inmemory.MemoryStorageConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class MemoryStorageConfigTest {

    @Test
    @DisplayName("기본 설정으로 생성")
    void shouldCreateWithDefaultSettings() {
        // When
        MemoryStorageConfig config = new MemoryStorageConfig();

        // Then
        assertThat(config.getType()).isEqualTo("memory");
        assertThat(config.getEntryTtl()).isEqualTo(Duration.ofHours(1));
        assertThat(config.getCleanupInterval()).isEqualTo(Duration.ofMinutes(10));
        assertThat(config.getMaxEntries()).isEqualTo(10000);
        assertThat(config.isEnableMetrics()).isTrue();
    }

    @Test
    @DisplayName("커스텀 설정으로 생성")
    void shouldCreateWithCustomSettings() {
        // Given
        Duration ttl = Duration.ofMinutes(30);
        Duration cleanup = Duration.ofMinutes(5);
        int maxEntries = 5000;

        // When
        MemoryStorageConfig config = new MemoryStorageConfig(ttl, cleanup, maxEntries, false);

        // Then
        assertThat(config.getEntryTtl()).isEqualTo(ttl);
        assertThat(config.getCleanupInterval()).isEqualTo(cleanup);
        assertThat(config.getMaxEntries()).isEqualTo(maxEntries);
        assertThat(config.isEnableMetrics()).isFalse();
    }

    @Test
    @DisplayName("팩토리 메소드 - TTL 설정")
    void shouldCreateWithTtl() {
        // Given
        Duration ttl = Duration.ofMinutes(15);

        // When
        MemoryStorageConfig config = MemoryStorageConfig.withTtl(ttl);

        // Then
        assertThat(config.getEntryTtl()).isEqualTo(ttl);
        assertThat(config.getMaxEntries()).isEqualTo(10000);
    }

    @Test
    @DisplayName("팩토리 메소드 - 무제한 설정")
    void shouldCreateUnlimitedConfig() {
        // When
        MemoryStorageConfig config = MemoryStorageConfig.unlimited();

        // Then
        assertThat(config.getEntryTtl()).isEqualTo(Duration.ofDays(365));
        assertThat(config.getMaxEntries()).isEqualTo(Integer.MAX_VALUE);
        assertThat(config.isEnableMetrics()).isFalse();
    }

    @Test
    @DisplayName("설정 검증 - 유효한 설정")
    void shouldValidateSuccessfully() {
        // Given
        MemoryStorageConfig config = new MemoryStorageConfig();

        // When & Then
        assertThatCode(config::validate).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("설정 검증 - 음수 TTL")
    void shouldRejectNegativeTtl() {
        // Given
        Duration negativeTtl = Duration.ofMinutes(-1);

        // When & Then
        assertThatThrownBy(() -> new MemoryStorageConfig(
                negativeTtl, Duration.ofMinutes(10), 1000, true
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Entry TTL cannot be negative");
    }

    @Test
    @DisplayName("설정 검증 - 음수 정리 간격")
    void shouldRejectNegativeCleanupInterval() {
        // Given
        Duration negativeCleanup = Duration.ofMinutes(-5);

        // When & Then
        assertThatThrownBy(() -> new MemoryStorageConfig(
                Duration.ofHours(1), negativeCleanup, 1000, true
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cleanup interval cannot be negative");
    }

    @Test
    @DisplayName("설정 검증 - 0 이하 최대 엔트리")
    void shouldRejectNonPositiveMaxEntries() {
        assertThatThrownBy(() -> new MemoryStorageConfig(
                Duration.ofHours(1), Duration.ofMinutes(10), 0, true
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Max entries must be positive");

        assertThatThrownBy(() -> new MemoryStorageConfig(
                Duration.ofHours(1), Duration.ofMinutes(10), -100, true
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Max entries must be positive");
    }

    @Test
    @DisplayName("성능 속성 반환")
    void shouldReturnPerformanceProperties() {
        // Given
        MemoryStorageConfig config = new MemoryStorageConfig(
                Duration.ofMinutes(30), Duration.ofMinutes(5), 5000, true
        );

        // When
        Map<String, Object> props = config.getPerformanceProperties();

        // Then
        assertThat(props).containsEntry("entryTtlMs", 30 * 60 * 1000L);
        assertThat(props).containsEntry("cleanupIntervalMs", 5 * 60 * 1000L);
        assertThat(props).containsEntry("maxEntries", 5000);
        assertThat(props).containsEntry("enableMetrics", true);
    }

    @Test
    @DisplayName("연결 속성은 빈 맵 반환")
    void shouldReturnEmptyConnectionProperties() {
        // Given
        MemoryStorageConfig config = new MemoryStorageConfig();

        // When
        Map<String, Object> props = config.getConnectionProperties();

        // Then
        assertThat(props).isEmpty();
    }
}