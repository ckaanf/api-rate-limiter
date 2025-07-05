package io.github.ckaanf.ratelimiter.core;

/**
 * Rate Limiter 프로바이더 인터페이스
 * 각 알고리즘 모듈에서 구현하여 SPI를 통해 등록
 */
public interface RateLimiterProvider {

    /**
     * 지원하는 알고리즘 타입
     */
    String getAlgorithmType();

    /**
     * Rate Limiter 생성
     *
     * @param config  설정
     * @param storage 저장소
     * @return Rate Limiter 인스턴스
     */
    RateLimiter create(RateLimiterConfig config, RateLimiterStorage storage);

    /**
     * 설정 지원 여부 확인
     */
    boolean supports(RateLimiterConfig config);

    /**
     * 프로바이더 우선순위 (높을수록 우선)
     */
    default int getPriority() {
        return 0;
    }
}