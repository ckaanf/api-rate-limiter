package io.github.ckaanf.ratelimiter.springboot.starter;

import io.github.ckaanf.ratelimiter.core.RateLimiter;
import io.github.ckaanf.ratelimiter.core.RateLimiterRegistry;
import io.github.ckaanf.ratelimiter.core.exceptions.RateLimitExceededException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class RateLimiterAspect {

    private final RateLimiterRegistry registry;

    public RateLimiterAspect(RateLimiterRegistry registry) {
        this.registry = registry;
    }

    @Around("@annotation(io.github.ckaanf.ratelimiter.springboot.starter.RateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimitAnnotation = method.getAnnotation(RateLimit.class);

        String limiterName = rateLimitAnnotation.limiterName();
        long cost = rateLimitAnnotation.cost();

        // cost 값이 0 이하인 경우, 잘못된 설정으로 간주하고 예외를 발생시킵니다.
        if (cost <= 0) {
            throw new IllegalArgumentException("RateLimit 'cost' must be a positive number.");
        }

        RateLimiter limiter = registry.findRateLimiter(limiterName)
                .orElseThrow(() -> new IllegalStateException(
                        "RateLimiter named '" + limiterName + "' is not configured."
                ));

        // cost 값만큼 토큰 소비를 시도합니다.
        if (limiter.tryConsume(cost)) {
            return joinPoint.proceed();
        } else {
            throw new RateLimitExceededException(
                    String.format("Rate limit exceeded for '%s'", limiterName),
                    cost, // 예외 메시지에도 실제 소비 시도한 cost 값을 전달합니다.
                    limiter.getAvailableTokens(),
                    null // waitTime은 여기서 계산하기 복잡하므로 생략
            );
        }
    }
}