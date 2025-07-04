package io.github.ckaanf.ratelimiter.springboot.autoconfigure;

import io.github.ckaanf.ratelimiter.core.RateLimiter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = RateLimiterAutoConfiguration.class)
@TestPropertySource(properties = {
    "rate-limiter.enabled=true",
    "rate-limiter.key=test-key"
})
public class RateLimiterAutoConfigurationTest {

    @Autowired
    private RateLimiter rateLimiter;

    @Test
    public void testRateLimiterBeanCreation() {
        assertNotNull(rateLimiter, "RateLimiter bean should be created");
    }

    @Test
    public void testRateLimiterTryConsume() {
        assertTrue(rateLimiter.tryConsume(), "RateLimiter should allow token consumption");
    }
} 