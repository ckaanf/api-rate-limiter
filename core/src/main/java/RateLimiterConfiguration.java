import java.time.Duration;

public interface RateLimiterConfiguration {
    long getCapacity();
    long getRefillTokens();
    Duration getRefillPeriod();
    String getAlgorithmType();
}