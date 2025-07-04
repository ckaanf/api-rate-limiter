package io.github.ckaanf.ratelimiter.springboot.starter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메서드에 Rate Limiting을 적용하는 어노테이션.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 적용할 Rate Limiter의 이름.
     * application.yml의 'rate-limiter.limiters'에 정의된 키와 일치해야 합니다.
     */
    String limiterName();

    /**
     * 이 요청이 소비할 토큰의 수 (가중치).
     * 기본값은 1입니다.
     */
    long cost() default 1;
}