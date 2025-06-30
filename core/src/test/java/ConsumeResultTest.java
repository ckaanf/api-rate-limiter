import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class ConsumeResultTest {

    @Test
    @DisplayName("성공한 소비 결과 생성")
    void shouldCreateConsumedResult() {
        // Given
        Instant nextRefill = Instant.now().plusSeconds(1);

        // When
        ConsumeResult result = ConsumeResult.consumed(10, 90, nextRefill);

        // Then
        assertThat(result.isConsumed()).isTrue();
        assertThat(result.isRejected()).isFalse();
        assertThat(result.getRequestedTokens()).isEqualTo(10);
        assertThat(result.getRemainingTokens()).isEqualTo(90);
        assertThat(result.getNextRefillTime()).isEqualTo(nextRefill);
        assertThat(result.getWaitTime()).isEqualTo(Duration.ZERO);
        assertThat(result.canRetryImmediately()).isTrue();
        assertThat(result.requiresWait()).isFalse();
        assertThat(result.getReason()).isNull();
    }

    @Test
    @DisplayName("거부된 소비 결과 생성")
    void shouldCreateRejectedResult() {
        // Given
        Duration waitTime = Duration.ofSeconds(2);
        Instant nextRefill = Instant.now().plusSeconds(1);

        // When
        ConsumeResult result = ConsumeResult.rejected(15, 5, waitTime, nextRefill);

        // Then
        assertThat(result.isConsumed()).isFalse();
        assertThat(result.isRejected()).isTrue();
        assertThat(result.getRequestedTokens()).isEqualTo(15);
        assertThat(result.getRemainingTokens()).isEqualTo(5);
        assertThat(result.getWaitTime()).isEqualTo(waitTime);
        assertThat(result.getNextRefillTime()).isEqualTo(nextRefill);
        assertThat(result.canRetryImmediately()).isFalse();
        assertThat(result.requiresWait()).isTrue();
        assertThat(result.getReason()).isNull();
    }

    @Test
    @DisplayName("사유와 함께 거부된 소비 결과 생성")
    void shouldCreateRejectedResultWithReason() {
        // Given
        Duration waitTime = Duration.ofMillis(500);
        Instant nextRefill = Instant.now().plusSeconds(1);
        String reason = "Insufficient tokens";

        // When
        ConsumeResult result = ConsumeResult.rejected(20, 3, waitTime, nextRefill, reason);

        // Then
        assertThat(result.isRejected()).isTrue();
        assertThat(result.getRequestedTokens()).isEqualTo(20);
        assertThat(result.getRemainingTokens()).isEqualTo(3);
        assertThat(result.getWaitTime()).isEqualTo(waitTime);
        assertThat(result.getReason()).isEqualTo(reason);
    }

    @Test
    @DisplayName("즉시 재시도 가능한 거부 결과")
    void shouldCreateRejectedResultWithoutWait() {
        // Given
        Instant nextRefill = Instant.now().plusSeconds(1);

        // When
        ConsumeResult result = ConsumeResult.rejected(5, 10, Duration.ZERO, nextRefill);

        // Then
        assertThat(result.isRejected()).isTrue();
        assertThat(result.canRetryImmediately()).isTrue();
        assertThat(result.requiresWait()).isFalse();
        assertThat(result.getWaitTime()).isEqualTo(Duration.ZERO);
    }

    @Test
    @DisplayName("평등성 검증")
    void shouldImplementEqualsAndHashCode() {
        // Given
        Instant nextRefill = Instant.now().plusSeconds(1);
        ConsumeResult result1 = ConsumeResult.consumed(10, 90, nextRefill);
        ConsumeResult result2 = ConsumeResult.consumed(10, 90, nextRefill);
        ConsumeResult result3 = ConsumeResult.consumed(5, 95, nextRefill);

        // Then
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isNotEqualTo(result3);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
    }

    @Test
    @DisplayName("toString 출력 확인")
    void shouldProvideReadableToString() {
        // Given
        Instant nextRefill = Instant.now().plusSeconds(1);
        ConsumeResult consumed = ConsumeResult.consumed(10, 90, nextRefill);
        ConsumeResult rejected = ConsumeResult.rejected(15, 5, Duration.ofSeconds(2), nextRefill, "Rate limit exceeded");

        // When & Then
        assertThat(consumed.toString())
                .contains("consumed=true")
                .contains("requested=10")
                .contains("remaining=90");

        assertThat(rejected.toString())
                .contains("consumed=false")
                .contains("requested=15")
                .contains("remaining=5")
                .contains("waitTime=PT2S")
                .contains("Rate limit exceeded");
    }
}