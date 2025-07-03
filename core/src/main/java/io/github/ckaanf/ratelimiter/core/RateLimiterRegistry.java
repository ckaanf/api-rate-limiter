package io.github.ckaanf.ratelimiter.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiter 중앙 레지스트리
 * SPI를 통해 프로바이더들을 자동 발견하고 관리
 */
public class RateLimiterRegistry {

    private static final RateLimiterRegistry INSTANCE = new RateLimiterRegistry();

    private final Map<String, RateLimiterProvider> algorithmProviders = new ConcurrentHashMap<>();
    private final Map<String, StorageProvider> storageProviders = new ConcurrentHashMap<>();
    private final Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

    private RateLimiterRegistry() {
        loadProviders();
    }

    // 테스트용 생성자
    private RateLimiterRegistry(boolean skipSpiLoading) {
        if (!skipSpiLoading) {
            loadProviders();
        }
    }

    public static RateLimiterRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * 테스트용 레지스트리 생성 (SPI 로딩 스킵)
     */
    public static RateLimiterRegistry createForTesting() {
        return new RateLimiterRegistry(true);
    }

    /**
     * Rate Limiter 생성 또는 기존 인스턴스 반환
     */
    public RateLimiter getRateLimiter(RateLimiterConfig config) {
        return rateLimiters.computeIfAbsent(config.getKey(),
                k -> createRateLimiter(config));
    }

    /**
     * 새로운 Rate Limiter 생성
     */
    public RateLimiter createRateLimiter(RateLimiterConfig config) {
        RateLimiterProvider algorithmProvider = getAlgorithmProvider(config);
        StorageProvider storageProvider = getStorageProvider(config);

        RateLimiterStorage storage = storageProvider.create(config.getStorageConfig());
        storage.initialize(config);

        return algorithmProvider.create(config, storage);
    }

    /**
     * 프로바이더 수동 등록
     */
    public void registerAlgorithmProvider(RateLimiterProvider provider) {
        algorithmProviders.put(provider.getAlgorithmType(), provider);
    }

    public void registerStorageProvider(StorageProvider provider) {
        storageProviders.put(provider.getStorageType(), provider);
    }

    /**
     * 레지스트리 초기화 (테스트용)
     */
    public void clear() {
        algorithmProviders.clear();
        storageProviders.clear();
        rateLimiters.clear();
    }

    private RateLimiterProvider getAlgorithmProvider(RateLimiterConfig config) {
        String algorithmType = config.getAlgorithmConfig().getType();
        RateLimiterProvider provider = algorithmProviders.get(algorithmType);

        if (provider == null) {
            throw new IllegalArgumentException("No provider found for algorithm: " + algorithmType);
        }

        if (!provider.supports(config)) {
            throw new IllegalArgumentException("Provider does not support config: " + config);
        }

        return provider;
    }

    private StorageProvider getStorageProvider(RateLimiterConfig config) {
        String storageType = config.getStorageConfig().getType();
        StorageProvider provider = storageProviders.get(storageType);

        if (provider == null) {
            throw new IllegalArgumentException("No provider found for storage: " + storageType);
        }

        if (!provider.supports(config.getStorageConfig())) {
            throw new IllegalArgumentException("Storage provider does not support config: " + config.getStorageConfig());
        }

        return provider;
    }

    /**
     * SPI를 통한 프로바이더 자동 로딩
     */
    private void loadProviders() {
        try {
            // Algorithm providers
            ServiceLoader<RateLimiterProvider> algorithmLoader =
                    ServiceLoader.load(RateLimiterProvider.class);

            for (RateLimiterProvider provider : algorithmLoader) {
                algorithmProviders.put(provider.getAlgorithmType(), provider);
            }

            // Storage providers
            ServiceLoader<StorageProvider> storageLoader =
                    ServiceLoader.load(StorageProvider.class);

            for (StorageProvider provider : storageLoader) {
                storageProviders.put(provider.getStorageType(), provider);
            }
        } catch (Exception e) {
            // SPI 로딩 실패는 로그만 남기고 계속 진행
            System.err.println("Failed to load providers via SPI: " + e.getMessage());
        }
    }

    /**
     * 등록된 프로바이더 정보
     */
    public Set<String> getAvailableAlgorithms() {
        return Collections.unmodifiableSet(algorithmProviders.keySet());
    }

    public Set<String> getAvailableStorages() {
        return Collections.unmodifiableSet(storageProviders.keySet());
    }

    /**
     * 디버깅용 정보
     */
    public Map<String, String> getProviderInfo() {
        Map<String, String> info = new HashMap<>();

        algorithmProviders.forEach((type, provider) ->
                info.put("algorithm." + type, provider.getClass().getName()));

        storageProviders.forEach((type, provider) ->
                info.put("storage." + type, provider.getClass().getName()));

        return info;
    }

    /**
     * 레지스트리 상태 정보
     */
    public RegistryStats getStats() {
        return new RegistryStats(
                algorithmProviders.size(),
                storageProviders.size(),
                rateLimiters.size()
        );
    }

    /**
     * 레지스트리 통계
     */
    public static class RegistryStats {
        private final int algorithmProviders;
        private final int storageProviders;
        private final int rateLimiters;

        public RegistryStats(int algorithmProviders, int storageProviders, int rateLimiters) {
            this.algorithmProviders = algorithmProviders;
            this.storageProviders = storageProviders;
            this.rateLimiters = rateLimiters;
        }

        public int getAlgorithmProviders() {
            return algorithmProviders;
        }

        public int getStorageProviders() {
            return storageProviders;
        }

        public int getRateLimiters() {
            return rateLimiters;
        }

        @Override
        public String toString() {
            return String.format("Registry{algorithms=%d, storages=%d, limiters=%d}",
                    algorithmProviders, storageProviders, rateLimiters);
        }
    }
}