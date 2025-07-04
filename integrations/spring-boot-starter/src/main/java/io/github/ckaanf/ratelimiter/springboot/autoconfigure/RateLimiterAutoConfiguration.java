// 파일: integrations/spring-boot-starter/src/main/java/io/github/ckaanf/ratelimiter/springboot/autoconfigure/RateLimiterAutoConfiguration.java

package io.github.ckaanf.ratelimiter.springboot.autoconfigure;

import io.github.ckaanf.ratelimiter.core.RateLimiter;
import io.github.ckaanf.ratelimiter.core.RateLimiterConfig;
import io.github.ckaanf.ratelimiter.core.RateLimiterRegistry;
import io.github.ckaanf.ratelimiter.springboot.autoconfigure.properties.RateLimiterProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Rate Limiter 자동 설정 클래스
 */
@Configuration
@EnableConfigurationProperties(RateLimiterProperties.class)
// "rate-limiter.enabled=true"일 때만 이 설정을 활성화합니다.
@ConditionalOnProperty(prefix = "rate-limiter", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RateLimiterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RateLimiter rateLimiter(RateLimiterProperties properties) {
        RateLimiterRegistry registry = RateLimiterRegistry.getInstance();

        // 주입받은 properties를 사용하여 RateLimiterConfig를 생성합니다.
        RateLimiterConfig config = new RateLimiterConfig(
                properties.getName(),           // application.properties의 'name'
                properties.getAlgorithmConfig(),// application.properties의 'algorithmConfig'
                properties.getStorageConfig()   // application.properties의 'storageConfig'
        );

        return registry.createRateLimiter(config);
    }
}