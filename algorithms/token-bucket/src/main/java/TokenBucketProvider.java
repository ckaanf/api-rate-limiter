/**
 * Token Bucket 알고리즘 프로바이더
 * SPI를 통해 자동 등록됨
 */
public class TokenBucketProvider implements RateLimiterProvider {

    @Override
    public String getAlgorithmType() {
        return "token-bucket";
    }

    @Override
    public RateLimiter create(RateLimiterConfig config, RateLimiterStorage storage) {
        return new StorageBasedTokenBucketRateLimiter(config.getKey(), config, storage);
    }

    @Override
    public boolean supports(RateLimiterConfig config) {
        return config.getAlgorithmConfig() instanceof TokenBucketAlgorithmConfig;
    }

    @Override
    public int getPriority() {
        return 100;
    }
}