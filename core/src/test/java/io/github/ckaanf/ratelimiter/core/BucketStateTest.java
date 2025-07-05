package io.github.ckaanf.ratelimiter.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class BucketStateTest {

    @Test
    @DisplayName("io.github.ckaanf.ratelimiter.core.BucketState 생성 및 값 확인")
    void shouldCreateBucketState() {
        // Given
        long availableTokens = 100;
        Instant lastRefill = Instant.now();
        long totalConsumed = 50;
        long totalRequested = 75;
        long rejectedRequests = 25;

        // When
        BucketState state = new BucketState(
                availableTokens, lastRefill, totalConsumed, totalRequested, rejectedRequests
        );

        // Then
        assertThat(state.getAvailableTokens()).isEqualTo(100);
        assertThat(state.getLastRefillTime()).isEqualTo(lastRefill);
        assertThat(state.getTotalConsumed()).isEqualTo(50);
        assertThat(state.getTotalRequested()).isEqualTo(75);
        assertThat(state.getRejectedRequests()).isEqualTo(25);
    }

    @Test
    @DisplayName("성공률 계산")
    void shouldCalculateSuccessRate() {
        // Given
        BucketState state = new BucketState(0, null, 80, 100, 20);

        // When
        double successRate = state.getSuccessRate();

        // Then
        assertThat(successRate).isEqualTo(0.8); // 80/100 = 0.8
    }

    @Test
    @DisplayName("요청이 없을 때 성공률은 0")
    void shouldReturnZeroSuccessRateWhenNoRequests() {
        // Given
        BucketState state = new BucketState(100, null, 0, 0, 0);

        // When
        double successRate = state.getSuccessRate();

        // Then
        assertThat(successRate).isEqualTo(0.0);
    }

    @Test
    @DisplayName("완전한 성공 시 성공률 1.0")
    void shouldReturnFullSuccessRate() {
        // Given
        BucketState state = new BucketState(50, null, 50, 50, 0);

        // When
        double successRate = state.getSuccessRate();

        // Then
        assertThat(successRate).isEqualTo(1.0); // 50/50 = 1.0
    }

    @Test
    @DisplayName("거부율 계산")
    void shouldCalculateRejectionRate() {
        // Given
        BucketState state = new BucketState(0, null, 70, 100, 30);

        // When
        double rejectionRate = state.getRejectionRate();

        // Then
        assertThat(rejectionRate).isEqualTo(0.3); // 30/100 = 0.3
    }

    @Test
    @DisplayName("요청이 없을 때 거부율은 0")
    void shouldReturnZeroRejectionRateWhenNoRequests() {
        // Given
        BucketState state = new BucketState(100, null, 0, 0, 0);

        // When
        double rejectionRate = state.getRejectionRate();

        // Then
        assertThat(rejectionRate).isEqualTo(0.0);
    }

    @Test
    @DisplayName("토큰 사용률 계산")
    void shouldCalculateUtilizationRate() {
        // Given
        BucketState state = new BucketState(25, null, 0, 0, 0); // 25개 남음
        long capacity = 100;

        // When
        double utilizationRate = state.getUtilizationRate(capacity);

        // Then
        assertThat(utilizationRate).isEqualTo(0.75); // (100-25)/100 = 0.75
    }

    @Test
    @DisplayName("토큰이 없을 때 사용률 100%")
    void shouldReturnFullUtilizationWhenEmpty() {
        // Given
        BucketState state = new BucketState(0, null, 100, 100, 0);
        long capacity = 100;

        // When
        double utilizationRate = state.getUtilizationRate(capacity);

        // Then
        assertThat(utilizationRate).isEqualTo(1.0);
    }

    @Test
    @DisplayName("빈 상태 확인")
    void shouldDetectEmptyState() {
        // Given
        BucketState emptyState = new BucketState(0, null, 100, 100, 0);
        BucketState nonEmptyState = new BucketState(50, null, 50, 100, 0);

        // Then
        assertThat(emptyState.isEmpty()).isTrue();
        assertThat(nonEmptyState.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("사용 여부 확인")
    void shouldDetectUsageHistory() {
        // Given
        BucketState usedState = new BucketState(80, null, 20, 20, 0);
        BucketState unusedState = new BucketState(100, null, 0, 0, 0);

        // Then
        assertThat(usedState.hasBeenUsed()).isTrue();
        assertThat(unusedState.hasBeenUsed()).isFalse();
    }

    @Test
    @DisplayName("평등성 검증")
    void shouldImplementEqualsAndHashCode() {
        // Given
        Instant now = Instant.now();
        BucketState state1 = new BucketState(100, now, 50, 75, 25);
        BucketState state2 = new BucketState(100, now, 50, 75, 25);
        BucketState state3 = new BucketState(90, now, 50, 75, 25);

        // Then
        assertThat(state1).isEqualTo(state2);
        assertThat(state1).isNotEqualTo(state3);
        assertThat(state1.hashCode()).isEqualTo(state2.hashCode());
    }

    @Test
    @DisplayName("toString 출력 확인")
    void shouldProvideReadableToString() {
        // Given
        BucketState state = new BucketState(75, null, 20, 25, 5);

        // When
        String result = state.toString();

        // Then
        assertThat(result)
                .contains("available=75")
                .contains("consumed=20")
                .contains("requested=25")
                .contains("rejected=5")
                .contains("successRate=80.00%"); // 20/25 = 0.8 = 80%
    }
}