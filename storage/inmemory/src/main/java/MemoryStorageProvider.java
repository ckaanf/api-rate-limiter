/**
 * 메모리 저장소 프로바이더
 * SPI를 통해 자동 등록됨
 */
public class MemoryStorageProvider implements StorageProvider {

    @Override
    public String getStorageType() {
        return "memory";
    }

    @Override
    public RateLimiterStorage create(StorageConfig config) {
        if (!(config instanceof MemoryStorageConfig)) {
            throw new IllegalArgumentException("Invalid storage config type: " + config.getClass());
        }

        return new InMemoryRateLimiterStorage((MemoryStorageConfig) config);
    }

    @Override
    public boolean supports(StorageConfig config) {
        return config instanceof MemoryStorageConfig;
    }

    @Override
    public int getPriority() {
        return 100; // 기본 구현체로 높은 우선순위
    }
}

