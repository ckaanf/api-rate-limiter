package io.github.ckaanf.ratelimiter.springboot.starter;

import io.github.ckaanf.ratelimiter.core.RateLimiter;
import io.github.ckaanf.ratelimiter.core.RateLimiterRegistry;
import io.github.ckaanf.ratelimiter.core.exceptions.RateLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RateLimiterAspectTest.TestController.class)
@Import({
        AopAutoConfiguration.class,
        RateLimiterAspect.class,
        RateLimiterAspectTest.TestConfig.class
})
class RateLimiterAspectTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RateLimiterRegistry rateLimiterRegistry;

    private RateLimiter mockRateLimiter;

    @BeforeEach
    void setUp() {
        mockRateLimiter = mock(RateLimiter.class);
        when(mockRateLimiter.tryConsume(anyLong())).thenReturn(true);
        when(rateLimiterRegistry.findRateLimiter(anyString())).thenReturn(Optional.of(mockRateLimiter));
    }

    @Test
    @DisplayName("기본 cost(1)로 RateLimit이 적용되어야 한다")
    void whenDefaultCost_thenTryConsumeWithOne() throws Exception {
        mockMvc.perform(get("/test/default-cost"))
                .andExpect(status().isOk());
        verify(mockRateLimiter).tryConsume(1L);
    }

    @Test
    @DisplayName("지정된 cost(5)로 RateLimit이 적용되어야 한다")
    void whenCustomCost_thenTryConsumeWithFive() throws Exception {
        mockMvc.perform(get("/test/custom-cost"))
                .andExpect(status().isOk());
        verify(mockRateLimiter).tryConsume(5L);
    }

    @Test
    @DisplayName("RateLimit 초과 시 429 Too Many Requests를 반환해야 한다")
    void whenRateLimitExceeded_thenReturns429() throws Exception {
        when(mockRateLimiter.tryConsume(3L)).thenReturn(false);
        when(mockRateLimiter.getAvailableTokens()).thenReturn(0L);

        mockMvc.perform(get("/test/exceeded"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value("Too Many Requests"))
                .andExpect(jsonPath("$.details").value("Rate limit exceeded for 'fail-limiter'"));

        verify(mockRateLimiter).tryConsume(3L);
    }

    @Test
    @DisplayName("cost가 0일 때 400 Bad Request를 반환해야 한다")
    void whenCostIsZero_thenReturns400() throws Exception {
        mockMvc.perform(get("/test/invalid-cost-zero"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("RateLimit 'cost' must be a positive number."));

        verify(mockRateLimiter, never()).tryConsume(anyLong());
    }

    @Test
    @DisplayName("cost가 음수일 때 400 Bad Request를 반환해야 한다")
    void whenCostIsNegative_thenReturns400() throws Exception {
        mockMvc.perform(get("/test/invalid-cost-negative"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.details").value("RateLimit 'cost' must be a positive number."));

        verify(mockRateLimiter, never()).tryConsume(anyLong());
    }

    // --- 테스트를 위한 내부 설정 및 컨트롤러 ---

    @Configuration
    static class TestConfig {
        @Bean
        public TestController testController() {
            return new TestController();
        }

        @Bean
        public TestGlobalExceptionHandler testGlobalExceptionHandler() {
            return new TestGlobalExceptionHandler();
        }
    }

    @RestController
    static class TestController {
        @GetMapping("/test/default-cost")
        @RateLimit(limiterName = "default-limiter")
        public String defaultCost() { return "OK"; }

        @GetMapping("/test/custom-cost")
        @RateLimit(limiterName = "custom-limiter", cost = 5)
        public String customCost() { return "OK"; }

        @GetMapping("/test/exceeded")
        @RateLimit(limiterName = "fail-limiter", cost = 3)
        public String exceeded() { return "Should not be reached"; }

        @GetMapping("/test/invalid-cost-zero")
        @RateLimit(limiterName = "invalid-limiter", cost = 0)
        public String invalidCostZero() { return "Should not be reached"; }

        @GetMapping("/test/invalid-cost-negative")
        @RateLimit(limiterName = "invalid-limiter", cost = -1)
        public String invalidCostNegative() { return "Should not be reached"; }
    }

    @RestControllerAdvice
    static class TestGlobalExceptionHandler {
        @ExceptionHandler(RateLimitExceededException.class)
        public ResponseEntity<Map<String, String>> handleRateLimitExceeded(RateLimitExceededException ex) {
            Map<String, String> body = Map.of(
                    "message", "Too Many Requests",
                    "details", ex.getMessage()
            );
            return new ResponseEntity<>(body, HttpStatus.TOO_MANY_REQUESTS);
        }

        /**
         * 잘못된 인자 값(예: cost <= 0)에 대한 예외를 처리합니다.
         * 400 Bad Request를 반환합니다.
         */
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
            Map<String, String> body = Map.of(
                    "message", "Bad Request",
                    "details", ex.getMessage()
            );
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }
}