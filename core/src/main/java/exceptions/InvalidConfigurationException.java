package exceptions;

/**
 * Rate Limiter 설정이 잘못된 경우 발생하는 예외
 */
public class InvalidConfigurationException extends RuntimeException {

    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidConfigurationException capacityMustBePositive(long capacity) {
        return new InvalidConfigurationException("Capacity must be positive, but was: " + capacity);
    }

    public static InvalidConfigurationException refillTokensMustBePositive(long refillTokens) {
        return new InvalidConfigurationException("Refill tokens must be positive, but was: " + refillTokens);
    }

    public static InvalidConfigurationException refillPeriodMustBePositive() {
        return new InvalidConfigurationException("Refill period must be positive");
    }

    public static InvalidConfigurationException initialTokensOutOfRange(long initialTokens, long capacity) {
        return new InvalidConfigurationException(
                String.format("Initial tokens must be between 0 and capacity (%d), but was: %d", capacity, initialTokens));
    }
}