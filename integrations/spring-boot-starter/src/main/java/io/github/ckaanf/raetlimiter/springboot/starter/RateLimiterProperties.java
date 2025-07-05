package io.github.ckaanf.raetlimiter.springboot.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ratelimiter", ignoreUnknownFields = false)
public class RateLimiterProperties {

    private boolean enabled = true;
    private String key = "default";
    private int capacity = 1000;
    private int refillTokens = 50;
    private int refillPeriod = 1;

    public int getInitialTokens() {
        return initialTokens;
    }

    public void setInitialTokens(int initialTokens) {
        this.initialTokens = initialTokens;
    }

    private int initialTokens = 500;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getRefillTokens() {
        return refillTokens;
    }

    public void setRefillTokens(int refillTokens) {
        this.refillTokens = refillTokens;
    }

    public int getRefillPeriod() {
        return refillPeriod;
    }

    public void setRefillPeriod(int refillPeriod) {
        this.refillPeriod = refillPeriod;
    }
}
