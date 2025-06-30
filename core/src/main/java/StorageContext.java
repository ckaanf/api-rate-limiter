/**
 * 저장소 작업 컨텍스트
 */
public class StorageContext {

    private final String key;
    private final long tokens;
    private final RateLimiterConfig config;
    private final OperationType operation;

    public enum OperationType {
        CONSUME, QUERY
    }

    private StorageContext(String key, long tokens, RateLimiterConfig config, OperationType operation) {
        this.key = key;
        this.tokens = tokens;
        this.config = config;
        this.operation = operation;
    }

    public static StorageContext forConsume(String key, long tokens, RateLimiterConfig config) {
        return new StorageContext(key, tokens, config, OperationType.CONSUME);
    }

    public static StorageContext forQuery(String key, RateLimiterConfig config) {
        return new StorageContext(key, 0, config, OperationType.QUERY);
    }

    // Getters
    public String getKey() {
        return key;
    }

    public long getTokens() {
        return tokens;
    }

    public RateLimiterConfig getConfig() {
        return config;
    }

    public OperationType getOperation() {
        return operation;
    }
}