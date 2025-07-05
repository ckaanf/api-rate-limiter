package io.github.ckaanf.ratelimiter.springexample.controller;

import io.github.ckaanf.ratelimiter.core.BucketState;
import io.github.ckaanf.ratelimiter.core.ConsumeResult;
import io.github.ckaanf.ratelimiter.core.RateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class TestController {

    RateLimiter rateLimiter;

    public TestController(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @GetMapping("/test")
    public String test() {
        // 3. 기본 사용법
        System.out.println("=== 기본 토큰 소비 테스트 ===");
        for (int i = 0; i < 5; i++) {
            if (rateLimiter.tryConsume()) {
                System.out.println("요청 " + (i + 1) + ": 허용됨 (남은 토큰: " + rateLimiter.getAvailableTokens() + ")");
            } else {
                System.out.println("요청 " + (i + 1) + ": 거부됨 (남은 토큰: " + rateLimiter.getAvailableTokens() + ")");
            }
        }

        // 4. 대량 토큰 소비
        System.out.println("\n=== 대량 토큰 소비 테스트 ===");
        long tokensToConsume = 50;
        if (rateLimiter.tryConsume(tokensToConsume)) {
            System.out.println(tokensToConsume + "개 토큰 소비 성공 (남은 토큰: " + rateLimiter.getAvailableTokens() + ")");
        } else {
            System.out.println(tokensToConsume + "개 토큰 소비 실패 (남은 토큰: " + rateLimiter.getAvailableTokens() + ")");
        }

        // 5. 대기 시간 지정 소비
        System.out.println("\n=== 대기 시간 지정 소비 테스트 ===");
        boolean result = rateLimiter.tryConsume(10, Duration.ofMillis(100));
        System.out.println("100ms 대기 후 10개 토큰 소비: " + (result ? "성공" : "실패"));

        // 6. 상세 결과 조회
        System.out.println("\n=== 상세 결과 조회 ===");
        ConsumeResult detailResult = rateLimiter.tryConsumeWithResult(5);
        System.out.println("소비 결과: " + detailResult);

        // 7. 버킷 상태 조회
        BucketState state = rateLimiter.getBucketState();
        System.out.println("현재 버킷 상태: " + state);

        return "test";
    }
}
