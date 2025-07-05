package io.github.ckaanf.ratelimiter.core;

/**
 * 저장소 프로바이더 인터페이스
 * 각 저장소 모듈에서 구현하여 SPI를 통해 등록
 */
public interface StorageProvider {

    /**
     * 지원하는 저장소 타입
     */
    String getStorageType();

    /**
     * 저장소 생성
     *
     * @param config 저장소 설정
     * @return 저장소 인스턴스
     */
    RateLimiterStorage create(StorageConfig config);

    /**
     * 설정 지원 여부 확인
     */
    boolean supports(StorageConfig config);

    /**
     * 프로바이더 우선순위 (높을수록 우선)
     */
    default int getPriority() {
        return 0;
    }
}